package com.codeforge.codeforge.problemset.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.problem.constant.ProblemStatus;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.mapper.ProblemMapper;
import com.codeforge.codeforge.problemset.dto.ProblemSetDetailVO;
import com.codeforge.codeforge.problemset.dto.ProblemSetRequest;
import com.codeforge.codeforge.problemset.dto.ProblemSetVO;
import com.codeforge.codeforge.problemset.entity.ProblemSet;
import com.codeforge.codeforge.problemset.entity.ProblemSetItem;
import com.codeforge.codeforge.problemset.mapper.ProblemSetItemMapper;
import com.codeforge.codeforge.problemset.mapper.ProblemSetMapper;
import com.codeforge.codeforge.problemset.service.ProblemSetService;
import com.codeforge.codeforge.user.dto.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemSetServiceImpl extends ServiceImpl<ProblemSetMapper, ProblemSet> implements ProblemSetService {
    private final ProblemSetItemMapper problemSetItemMapper;
    private final ProblemMapper problemMapper;

    @Override
    @Transactional
    public ProblemSetVO createProblemSet(ProblemSetRequest request) {
        UserVO current = UserContext.get();
        ProblemSet set = new ProblemSet();
        set.setTitle(request.getTitle());
        set.setDescription(request.getDescription());
        set.setCreatorId(current.getId());
        set.setIsPublic(Boolean.TRUE.equals(request.getIsPublic()) ? 1 : 0);
        save(set);

        saveItems(set.getId(), request.getProblemIds());
        return toVO(set, problemIdsSize(request));
    }


    @Override
    public Page<ProblemSetVO> listProblemSets(Integer page, Integer size) {
        UserVO current = UserContext.get();
        // 可见：公开题单 + 我自己建的题单
        LambdaQueryWrapper<ProblemSet> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(ProblemSet::getIsPublic, 1)
                .or()
                .eq(ProblemSet::getCreatorId, current.getId()));
        wrapper.orderByDesc(ProblemSet::getId);
        Page<ProblemSet> result = page(new Page<>(page, size), wrapper);

        Page<ProblemSetVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ProblemSetVO> voList = new ArrayList<>();
        for (ProblemSet set : result.getRecords()) {
            voList.add(toVO(set, countPublishedProblems(set.getId())));
        }
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ProblemSetDetailVO getProblemSetDetail(Long id) {
        ProblemSet set = getById(id);
        if (set == null) {
            throw new BusinessException(ErrorCode.PROBLEM_SET_NOT_FOUND);
        }

        UserVO current = UserContext.get();
        boolean isPublic = set.getIsPublic() != null && set.getIsPublic() == 1;
        boolean isCreator = set.getCreatorId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        // 私有题单：只有创建者本人或管理员能看，其他人当不存在
        if (!isPublic && !isCreator && !isAdmin) {
            throw new BusinessException(ErrorCode.PROBLEM_SET_NOT_FOUND);
        }

        List<ProblemSetItem> items = problemSetItemMapper.selectList(
                new LambdaQueryWrapper<ProblemSetItem>()
                        .eq(ProblemSetItem::getSetId, id)
                        .orderByAsc(ProblemSetItem::getSortNo));

        List<ProblemVO> problems = new ArrayList<>();
        for (ProblemSetItem item : items) {
            Problem problem = problemMapper.selectById(item.getProblemId());
            // 只展示仍处于「已发布」状态的题；下架/删除的题不再出现在题单里
            if (problem != null && ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
                problems.add(toProblemVO(problem));
            }
        }

        ProblemSetDetailVO vo = new ProblemSetDetailVO();
        vo.setId(set.getId());
        vo.setTitle(set.getTitle());
        vo.setDescription(set.getDescription());
        vo.setCreatorId(set.getCreatorId());
        vo.setIsPublic(isPublic);
        vo.setCreateTime(set.getCreateTime());
        vo.setProblems(problems);
        return vo;
    }

    @Override
    @Transactional
    public ProblemSetVO updateProblemSet(Long id, ProblemSetRequest request) {
        ProblemSet set = getById(id);
        if (set == null) {
            throw new BusinessException(ErrorCode.PROBLEM_SET_NOT_FOUND);
        }
        checkOwnerOrAdmin(set);

        set.setTitle(request.getTitle());
        set.setDescription(request.getDescription());
        set.setIsPublic(Boolean.TRUE.equals(request.getIsPublic()) ? 1 : 0);
        set.setUpdateTime(LocalDateTime.now());
        updateById(set);

        // 重建关联：先清空旧题目，再按新列表重新插入
        problemSetItemMapper.delete(new LambdaQueryWrapper<ProblemSetItem>()
                .eq(ProblemSetItem::getSetId, id));
        saveItems(id, request.getProblemIds());

        return toVO(set, problemIdsSize(request));
    }

    @Override
    @Transactional
    public void deleteProblemSet(Long id) {
        ProblemSet set = getById(id);
        if (set == null) {
            throw new BusinessException(ErrorCode.PROBLEM_SET_NOT_FOUND);
        }
        checkOwnerOrAdmin(set);

        removeById(id); // 逻辑删除（@TableLogic 自动转 update deleted=1）
        problemSetItemMapper.delete(new LambdaQueryWrapper<ProblemSetItem>()
                .eq(ProblemSetItem::getSetId, id)); // 关联记录物理删除
    }

    // 只有创建者本人或管理员能改/删
    private void checkOwnerOrAdmin(ProblemSet set) {
        UserVO current = UserContext.get();
        boolean isOwner = set.getCreatorId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    // 把题目 ID 列表按顺序写入关联表；只允许加已发布的题
    private void saveItems(Long setId, List<Long> problemIds) {
        if (problemIds == null || problemIds.isEmpty()) {
            return;
        }
        int sortNo = 1;
        for (Long problemId : problemIds) {
            Problem problem = problemMapper.selectById(problemId);
            if (problem == null || !ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }
            ProblemSetItem item = new ProblemSetItem();
            item.setSetId(setId);
            item.setProblemId(problemId);
            item.setSortNo(sortNo++);
            problemSetItemMapper.insert(item);
        }
    }

    // 统计题单里仍处于「已发布」状态的题数（下架/删除的不算）
    private long countPublishedProblems(Long setId) {
        List<ProblemSetItem> items = problemSetItemMapper.selectList(
                new LambdaQueryWrapper<ProblemSetItem>().eq(ProblemSetItem::getSetId, setId));
        if (items.isEmpty()) {
            return 0;
        }
        List<Long> problemIds = new ArrayList<>();
        for (ProblemSetItem item : items) {
            problemIds.add(item.getProblemId());
        }
        return problemMapper.selectCount(
                new LambdaQueryWrapper<Problem>()
                        .in(Problem::getId, problemIds)
                        .eq(Problem::getStatus, ProblemStatus.PUBLISHED));
    }

    private long problemIdsSize(ProblemSetRequest request) {
        return request.getProblemIds() == null ? 0 : request.getProblemIds().size();
    }

    private ProblemSetVO toVO(ProblemSet set, long problemCount) {
        ProblemSetVO vo = new ProblemSetVO();
        vo.setId(set.getId());
        vo.setTitle(set.getTitle());
        vo.setDescription(set.getDescription());
        vo.setCreatorId(set.getCreatorId());
        vo.setIsPublic(set.getIsPublic() != null && set.getIsPublic() == 1);
        vo.setProblemCount(problemCount);
        vo.setCreateTime(set.getCreateTime());
        return vo;
    }

    private ProblemVO toProblemVO(Problem problem) {
        ProblemVO vo = new ProblemVO();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        return vo;
    }

}
