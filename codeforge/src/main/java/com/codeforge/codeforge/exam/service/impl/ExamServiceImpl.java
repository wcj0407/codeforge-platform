package com.codeforge.codeforge.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.exam.dto.ExamDetailVO;
import com.codeforge.codeforge.exam.dto.ExamRequest;
import com.codeforge.codeforge.exam.dto.ExamVO;
import com.codeforge.codeforge.exam.dto.RankItemVO;
import com.codeforge.codeforge.exam.entity.Exam;
import com.codeforge.codeforge.exam.entity.ExamProblem;
import com.codeforge.codeforge.exam.mapper.ExamMapper;
import com.codeforge.codeforge.exam.mapper.ExamProblemMapper;
import com.codeforge.codeforge.exam.service.ExamService;
import com.codeforge.codeforge.problem.constant.ProblemStatus;
import com.codeforge.codeforge.problem.dto.ProblemVO;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.mapper.ProblemMapper;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {
    private final ExamProblemMapper examProblemMapper;
    private final ProblemMapper problemMapper;
    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public ExamVO createExam(ExamRequest request) {
        // 结束时间必须晚于开始时间
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        UserVO current = UserContext.get();
        Exam exam = new Exam();
        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setCreatorId(current.getId());
        save(exam);

        saveExamProblems(exam.getId(), request.getProblemIds());
        return toVO(exam, problemIdsSize(request));
    }

    @Override
    @Transactional
    public ExamVO updateExam(Long id, ExamRequest request) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException(ErrorCode.EXAM_NOT_FOUND);
        }
        checkOwnerOrAdmin(exam);
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }

        exam.setTitle(request.getTitle());
        exam.setDescription(request.getDescription());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setUpdateTime(LocalDateTime.now());
        updateById(exam);

        // 重建题目关联：先清空旧关联，再按新列表重新插入
        examProblemMapper.delete(new LambdaQueryWrapper<ExamProblem>()
                .eq(ExamProblem::getExamId, id));
        saveExamProblems(id, request.getProblemIds());

        return toVO(exam, problemIdsSize(request));
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException(ErrorCode.EXAM_NOT_FOUND);
        }
        checkOwnerOrAdmin(exam);

        removeById(id); // 逻辑删除
        examProblemMapper.delete(new LambdaQueryWrapper<ExamProblem>()
                .eq(ExamProblem::getExamId, id)); // 关联记录物理删除
    }

    // 只有创建者本人或管理员能改/删
    private void checkOwnerOrAdmin(Exam exam) {
        UserVO current = UserContext.get();
        boolean isOwner = exam.getCreatorId().equals(current.getId());
        boolean isAdmin = "ADMIN".equals(current.getRole());
        if (!isOwner && !isAdmin) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    @Override
    public Page<ExamVO> listExams(Integer page, Integer size) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Exam::getId);
        Page<Exam> result = page(new Page<>(page, size), wrapper);

        Page<ExamVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<ExamVO> voList = new ArrayList<>();
        for (Exam exam : result.getRecords()) {
            Long count = examProblemMapper.selectCount(
                    new LambdaQueryWrapper<ExamProblem>().eq(ExamProblem::getExamId, exam.getId()));
            voList.add(toVO(exam, count));
        }
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public ExamDetailVO getExamDetail(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException(ErrorCode.EXAM_NOT_FOUND);
        }

        List<ExamProblem> items = examProblemMapper.selectList(
                new LambdaQueryWrapper<ExamProblem>()
                        .eq(ExamProblem::getExamId, id)
                        .orderByAsc(ExamProblem::getSortNo));

        List<ProblemVO> problems = new ArrayList<>();
        for (ExamProblem item : items) {
            Problem problem = problemMapper.selectById(item.getProblemId());
            // 只展示仍处于「已发布」状态的题
            if (problem != null && ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
                problems.add(toProblemVO(problem));
            }
        }

        ExamDetailVO vo = new ExamDetailVO();
        vo.setId(exam.getId());
        vo.setTitle(exam.getTitle());
        vo.setDescription(exam.getDescription());
        vo.setStartTime(exam.getStartTime());
        vo.setEndTime(exam.getEndTime());
        vo.setStatus(calcStatus(exam));
        vo.setCreatorId(exam.getCreatorId());
        vo.setCreateTime(exam.getCreateTime());
        vo.setProblems(problems);
        vo.setSolvedProblemIds(loadSolvedProblemIds(exam, problems));
        return vo;
    }

    // 查当前用户在这场考核里已通过的题目 id（窗口内 ACCEPTED，去重）
    private List<Long> loadSolvedProblemIds(Exam exam, List<ProblemVO> problems) {
        List<Long> problemIds = new ArrayList<>();
        for (ProblemVO p : problems) {
            problemIds.add(p.getId());
        }
        if (problemIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<Submission> accepted = submissionMapper.selectList(
                new LambdaQueryWrapper<Submission>()
                        .in(Submission::getProblemId, problemIds)
                        .eq(Submission::getStatus, "ACCEPTED")
                        .eq(Submission::getUserId, UserContext.get().getId())
                        .ge(Submission::getSubmitTime, exam.getStartTime())
                        .le(Submission::getSubmitTime, exam.getEndTime()));
        Set<Long> solved = new HashSet<>();
        for (Submission s : accepted) {
            solved.add(s.getProblemId());
        }
        return new ArrayList<>(solved);
    }

    @Override
    public List<RankItemVO> getRank(Long id) {
        Exam exam = getById(id);
        if (exam == null) {
            throw new BusinessException(ErrorCode.EXAM_NOT_FOUND);
        }

        // 1. 拿这场考核的所有题目 id
        List<ExamProblem> items = examProblemMapper.selectList(
                new LambdaQueryWrapper<ExamProblem>().eq(ExamProblem::getExamId, id));
        List<Long> problemIds = new ArrayList<>();
        for (ExamProblem item : items) {
            problemIds.add(item.getProblemId());
        }
        if (problemIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 2. 查窗口内、这些题、ACCEPTED 的提交
        List<Submission> accepted = submissionMapper.selectList(
                new LambdaQueryWrapper<Submission>()
                        .in(Submission::getProblemId, problemIds)
                        .eq(Submission::getStatus, "ACCEPTED")
                        .ge(Submission::getSubmitTime, exam.getStartTime())
                        .le(Submission::getSubmitTime, exam.getEndTime()));

        // 3. 按用户分组，每个用户对每道题只算一次通过（Set 去重）
        Map<Long, Set<Long>> solved = new HashMap<>();
        for (Submission s : accepted) {
            solved.computeIfAbsent(s.getUserId(), k -> new HashSet<>()).add(s.getProblemId());
        }

        // 4. 转成榜单：查用户名 + 按通过题数降序排
        List<RankItemVO> rank = new ArrayList<>();
        for (Map.Entry<Long, Set<Long>> e : solved.entrySet()) {
            RankItemVO vo = new RankItemVO();
            vo.setUserId(e.getKey());
            vo.setSolvedCount(e.getValue().size());
            User user = userMapper.selectById(e.getKey());
            vo.setUsername(user != null ? user.getUsername() : "未知用户");
            rank.add(vo);
        }
        rank.sort((a, b) -> b.getSolvedCount() - a.getSolvedCount());
        for (int i = 0; i < rank.size(); i++) {
            rank.get(i).setRank(i + 1);
        }
        return rank;
    }

    // 把题目列表按顺序写入关联表；只允许已发布的题
    private void saveExamProblems(Long examId, List<Long> problemIds) {
        if (problemIds == null || problemIds.isEmpty()) {
            return;
        }
        int sortNo = 1;
        for (Long problemId : problemIds) {
            Problem problem = problemMapper.selectById(problemId);
            if (problem == null || !ProblemStatus.PUBLISHED.equals(problem.getStatus())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }
            ExamProblem item = new ExamProblem();
            item.setExamId(examId);
            item.setProblemId(problemId);
            item.setSortNo(sortNo++);
            examProblemMapper.insert(item);
        }
    }

    // 考核状态由时间实时推导
    private String calcStatus(Exam exam) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(exam.getStartTime())) return "NOT_STARTED";
        if (now.isAfter(exam.getEndTime())) return "ENDED";
        return "RUNNING";
    }

    private long problemIdsSize(ExamRequest request) {
        return request.getProblemIds() == null ? 0 : request.getProblemIds().size();
    }

    private ExamVO toVO(Exam exam, long problemCount) {
        ExamVO vo = new ExamVO();
        vo.setId(exam.getId());
        vo.setTitle(exam.getTitle());
        vo.setDescription(exam.getDescription());
        vo.setStartTime(exam.getStartTime());
        vo.setEndTime(exam.getEndTime());
        vo.setStatus(calcStatus(exam));
        vo.setProblemCount(problemCount);
        vo.setCreateTime(exam.getCreateTime());
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