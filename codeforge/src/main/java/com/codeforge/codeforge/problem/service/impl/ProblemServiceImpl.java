package com.codeforge.codeforge.problem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.problem.constant.ProblemStatus;
import com.codeforge.codeforge.problem.dto.ProblemCreateRequest;
import com.codeforge.codeforge.problem.dto.ProblemDetailVO;
import com.codeforge.codeforge.problem.dto.ProblemUpdateRequest;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.dto.ReviewRequest;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.entity.ProblemTestCase;
import com.codeforge.codeforge.problem.entity.ProblemVersion;
import com.codeforge.codeforge.problem.mapper.ProblemMapper;
import com.codeforge.codeforge.problem.mapper.ProblemTestCaseMapper;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.problem.service.ProblemService;
import com.codeforge.codeforge.user.dto.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProblemServiceImpl extends ServiceImpl<ProblemMapper, Problem> implements ProblemService {
    private final ProblemMapper problemMapper;
    private final ProblemVersionMapper problemVersionMapper;
    private final ProblemTestCaseMapper problemTestCaseMapper;
    @Override
    @Transactional
    public ProblemVO createProblem(ProblemCreateRequest request) {
        Problem problem = new Problem();
        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags());
        problem.setStatus(ProblemStatus.DRAFT);
        problem.setCreatorId(UserContext.get().getId());
        save(problem);

        ProblemVersion version = new ProblemVersion ();
        version.setProblemId(problem.getId());
        version.setVersion( 1 );
        version.setDescription(request.getDescription());
        version.setInputFormat(request.getInputFormat());
        version.setOutputFormat(request.getOutputFormat());
        version.setTimeLimit(request.getTimeLimit());
        version.setMemoryLimit(request.getMemoryLimit());
        version.setCreatedBy(UserContext.get().getId());
        problemVersionMapper.insert(version);

        // 用例写入独立表（公开/隐藏标记以 problem_test_case.is_public 为准）
        saveTestCases(version.getId(), request.getTestCases());

        ProblemVO vo = new ProblemVO ();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setCreatorId(problem.getCreatorId());
        return vo;
    }

    @Override
    public Page<ProblemVO> listProblems(Integer page, Integer size, String difficulty, String status, String keyword) {
        UserVO current = UserContext.get();
        LambdaQueryWrapper<Problem> queryWrapper = new LambdaQueryWrapper<>();

        // 公开题目列表：所有角色默认只看「已发布」的题
        if (status != null && "ADMIN".equals(current.getRole())) {
            // 仅管理员能按状态筛选（审核页查 PENDING_REVIEW 用）
            queryWrapper.eq(Problem::getStatus, status);
        } else {
            queryWrapper.eq(Problem::getStatus, ProblemStatus.PUBLISHED);
        }

        queryWrapper.eq(difficulty != null, Problem::getDifficulty, difficulty);
        // 按标题模糊搜索（大小写不敏感，取决于库的 utf8mb4_unicode_ci 排序规则）
        if (keyword != null && !keyword.isBlank()) {
            queryWrapper.like(Problem::getTitle, keyword.trim());
        }
        queryWrapper.orderByDesc(Problem::getId);
        Page<Problem> result = page(new Page<>(page, size), queryWrapper);
        return toVOPage(result);
    }

    @Override
    public Page<ProblemVO> listMyProblems(Integer page, Integer size) {
        Long myId = UserContext.get().getId();
        LambdaQueryWrapper<Problem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Problem::getCreatorId, myId);
        wrapper.orderByDesc(Problem::getId);
        Page<Problem> result = page(new Page<>(page, size), wrapper);
        return toVOPage(result);
    }

    // 把题目分页转成 VO 分页（公开列表、我的题目共用）
    private Page<ProblemVO> toVOPage(Page<Problem> result) {
        Page<ProblemVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ProblemVO> voList = new ArrayList<>();
        for (Problem problem : result.getRecords()) {
            voList.add(toVO(problem));
        }
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ProblemDetailVO getProblemDetail(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
        UserVO current = UserContext.get();

        boolean isPUBLISHED = ProblemStatus.PUBLISHED.equals(problem.getStatus());
        boolean isCreator = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());

        if (!isPUBLISHED && !isCreator && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }


        LambdaQueryWrapper<ProblemVersion> wrapper = new LambdaQueryWrapper<ProblemVersion>().eq(ProblemVersion::getProblemId, id).orderByDesc(ProblemVersion::getVersion).last("limit 1");
        ProblemVersion version = problemVersionMapper.selectOne(wrapper);
        ProblemDetailVO vo = new ProblemDetailVO();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setReviewComment(problem.getReviewComment());
        vo.setCreatorId(problem.getCreatorId());
        vo.setCreateTime(problem.getCreateTime());
        vo.setUpdateTime(problem.getUpdateTime());
        vo.setVersion(version.getVersion());
        vo.setDescription(version.getDescription());
        vo.setInputFormat(version.getInputFormat());
        vo.setOutputFormat(version.getOutputFormat());
        vo.setTimeLimit(version.getTimeLimit());
        vo.setMemoryLimit(version.getMemoryLimit());
        // 读取全量用例，下发时再按角色过滤
        vo.setTestCases(loadTestCases(version.getId(), false));
        vo.setCreatedAt(version.getCreatedAt());

        return applyVisibility(vo);
    }

    /**
     * 普通用户只保留公开样例；作者/管理员返回全量用例。
     * 题目本身的可见性（未发布题）已在查询前校验。
     */
    private ProblemDetailVO applyVisibility(ProblemDetailVO detail) {
        UserVO current = UserContext.get();
        boolean isCreator = detail.getCreatorId() != null && detail.getCreatorId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());

        if (!isCreator && !isAdmin && detail.getTestCases() != null) {
            detail.setTestCases(detail.getTestCases().stream()
                    .filter(tc -> Boolean.TRUE.equals(tc.getIsPublic()))
                    .toList());
        }
        return detail;
    }

    @Override
    @Transactional
    public ProblemVO updateProblem(Long id, ProblemUpdateRequest request) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }
         UserVO current = UserContext.get();
        boolean isowner = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());

        if (!isowner && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        // 编辑被驳回的题：状态回草稿，重新走提交审核
        if (ProblemStatus.REJECTED.equals(problem.getStatus())) {
            problem.setStatus(ProblemStatus.DRAFT);
            problem.setReviewComment(null);
        }

        LambdaQueryWrapper<ProblemVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProblemVersion::getProblemId, id)
                .orderByDesc(ProblemVersion::getVersion)
                .last( "limit 1" );
        ProblemVersion latest = problemVersionMapper.selectOne(wrapper);
        ProblemVersion version = new ProblemVersion();
        version.setProblemId(id);
        version.setVersion(latest.getVersion()+1);
        version.setDescription(request.getDescription());
        version.setInputFormat(request.getInputFormat());
        version.setOutputFormat(request.getOutputFormat());
        version.setTimeLimit(request.getTimeLimit());
        version.setMemoryLimit(request.getMemoryLimit());
        version.setCreatedBy(current.getId());
        problemVersionMapper.insert(version);

        // 用例写入独立表
        saveTestCases(version.getId(), request.getTestCases());

        problem.setTitle(request.getTitle());
        problem.setDifficulty(request.getDifficulty());
        problem.setTags(request.getTags());
        problem.setUpdateTime(LocalDateTime.now());
        updateById(problem);

        return toVO(problem);
    }

    @Override
    @Transactional
    public ProblemVO submitForReview(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        UserVO current = UserContext.get();
        // 只有作者本人能提交自己的题目进入审核
        if (!problem.getCreatorId().equals(current.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        // 只有草稿状态才允许提交审核
        if (!ProblemStatus.DRAFT.equals(problem.getStatus())) {
            throw new BusinessException(ErrorCode.PROBLEM_STATUS_ERROR);
        }

        problem.setStatus(ProblemStatus.PENDING_REVIEW);
        problem.setUpdateTime(LocalDateTime.now());
        updateById(problem);
        return toVO(problem);
    }

    @Override
    @Transactional
    public ProblemVO reviewProblem(Long id, ReviewRequest request) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        // 只有待审核状态才能被审核
        if (!ProblemStatus.PENDING_REVIEW.equals(problem.getStatus())) {
            throw new BusinessException(ErrorCode.PROBLEM_STATUS_ERROR);
        }


        boolean approved = Boolean.TRUE.equals(request.getApproved());
        if (approved) {
            problem.setStatus(ProblemStatus.PUBLISHED);
            problem.setReviewComment(null); // 通过时清空历史驳回原因
        } else {
            // 驳回必须填写原因
            if (request.getComment() == null || request.getComment().isBlank()) {
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }
            problem.setStatus(ProblemStatus.REJECTED);
            problem.setReviewComment(request.getComment());
        }

        problem.setUpdateTime(LocalDateTime.now());
        updateById(problem);
        return toVO(problem);
    }

    @Override
    @Transactional
    public ProblemVO offline(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        UserVO current = UserContext.get();
        boolean isOwner = problem.getCreatorId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        // 只有已发布状态才能下架
        if (!ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
            throw new BusinessException(ErrorCode.PROBLEM_STATUS_ERROR);
        }

        problem.setStatus(ProblemStatus.OFFLINE);
        problem.setUpdateTime(LocalDateTime.now());
        updateById(problem);
        return toVO(problem);
    }

    @Override
    public void deleteProblem(Long id) {
        Problem problem = getById(id);
        if (problem == null) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        UserVO current = UserContext.get();
        boolean isowner = problem.getCreatorId().equals(current.getId());
        boolean isadmin = "ADMIN".equals(current.getRole());
        if (!isowner && !isadmin) {
            throw new BusinessException(ErrorCode.PROBLEM_NOT_FOUND);
        }

        removeById(id);

    }

    /**
     * 把提交的用例列表写入 problem_test_case 表。
     * isPublic 为 null 或 true 视为公开，仅显式 false 视为隐藏。
     */
    private void saveTestCases(Long versionId, List<ProblemVersion.TestCase> testCases) {
        if (testCases == null) {
            return;
        }
        int sortNo = 1;
        for (ProblemVersion.TestCase tc : testCases) {
            ProblemTestCase row = new ProblemTestCase();
            row.setProblemVersionId(versionId);
            row.setSortNo(sortNo++);
            row.setInput(tc.getInput());
            row.setOutput(tc.getOutput());
            row.setIsPublic(tc.getIsPublic() == null || tc.getIsPublic() ? 1 : 0);
            problemTestCaseMapper.insert(row);
        }
    }

    /**
     * 从 problem_test_case 表读取指定版本的用例。
     * onlyPublic 为 true 时只返回公开样例（普通用户），否则返回全量（作者/管理员）。
     */
    private List<ProblemVersion.TestCase> loadTestCases(Long versionId, boolean onlyPublic) {
        LambdaQueryWrapper<ProblemTestCase> wrapper = new LambdaQueryWrapper<ProblemTestCase>()
                .eq(ProblemTestCase::getProblemVersionId, versionId);
        if (onlyPublic) {
            wrapper.eq(ProblemTestCase::getIsPublic, 1);
        }
        wrapper.orderByAsc(ProblemTestCase::getSortNo);
        List<ProblemTestCase> rows = problemTestCaseMapper.selectList(wrapper);
        List<ProblemVersion.TestCase> result = new ArrayList<>();
        for (ProblemTestCase row : rows) {
            ProblemVersion.TestCase tc = new ProblemVersion.TestCase();
            tc.setInput(row.getInput());
            tc.setOutput(row.getOutput());
            tc.setIsPublic(row.getIsPublic() != null && row.getIsPublic() == 1);
            result.add(tc);
        }
        return result;
    }

    private ProblemVO toVO(Problem problem) {
        ProblemVO vo = new ProblemVO ();
        vo.setId(problem.getId());
        vo.setTitle(problem.getTitle());
        vo.setDifficulty(problem.getDifficulty());
        vo.setTags(problem.getTags());
        vo.setStatus(problem.getStatus());
        vo.setReviewComment(problem.getReviewComment());
        vo.setCreatorId(problem.getCreatorId());
        vo.setCreateTime(problem.getCreateTime());
        vo.setUpdateTime(problem.getUpdateTime());
        return vo;
    }
}
