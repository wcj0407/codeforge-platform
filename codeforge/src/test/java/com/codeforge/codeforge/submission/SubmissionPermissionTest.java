package com.codeforge.codeforge.submission;

import com.codeforge.codeforge.common.ErrorCode;
import com.codeforge.codeforge.common.context.UserContext;
import com.codeforge.codeforge.common.exception.BusinessException;
import com.codeforge.codeforge.outbox.mapper.OutboxEventMapper;
import com.codeforge.codeforge.problem.entity.Problem;
import com.codeforge.codeforge.problem.mapper.ProblemVersionMapper;
import com.codeforge.codeforge.problem.service.ProblemService;
import com.codeforge.codeforge.submission.dto.SubmissionDetailVO;
import com.codeforge.codeforge.submission.entity.Submission;
import com.codeforge.codeforge.submission.mapper.SubmissionMapper;
import com.codeforge.codeforge.submission.service.impl.SubmissionServiceImpl;
import com.codeforge.codeforge.user.dto.UserVO;
import com.codeforge.codeforge.user.entity.User;
import com.codeforge.codeforge.user.mapper.UserMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * 提交详情权限测试：本人可看、他人不可看、管理员可看他人提交。
 */
@ExtendWith(MockitoExtension.class)
class SubmissionPermissionTest {

    private static final long SUBMISSION_ID = 10L;
    private static final long OWNER_ID = 5L;

    @Mock
    private SubmissionMapper submissionMapper;
    @Mock
    private ProblemService problemService;
    @Mock
    private ProblemVersionMapper problemVersionMapper;
    @Mock
    private OutboxEventMapper outboxEventMapper;
    @Mock
    private UserMapper userMapper;

    private SubmissionServiceImpl submissionService;

    @BeforeEach
    void setUp() {
        submissionService = new SubmissionServiceImpl(
                problemService, problemVersionMapper, outboxEventMapper, userMapper);
        ReflectionTestUtils.setField(submissionService, "baseMapper", submissionMapper);

        Submission submission = new Submission();
        submission.setId(SUBMISSION_ID);
        submission.setUserId(OWNER_ID);
        submission.setProblemId(100L);
        submission.setCode("public class Solution {}");
        submission.setLanguage("JAVA");
        submission.setProblemVersion(1);
        when(submissionMapper.selectById(SUBMISSION_ID)).thenReturn(submission);
    }

    private void stubProblem() {
        Problem problem = new Problem();
        problem.setId(100L);
        problem.setTitle("两数之和");
        when(problemService.getById(100L)).thenReturn(problem);
    }

    @AfterEach
    void clearContext() {
        UserContext.clear();
    }

    @Test
    void ownerCanViewOwnSubmission() {
        UserContext.set(user(OWNER_ID, "USER", "alice"));
        User owner = new User();
        owner.setId(OWNER_ID);
        owner.setUsername("alice");
        when(userMapper.selectById(OWNER_ID)).thenReturn(owner);
        stubProblem();

        SubmissionDetailVO detail = submissionService.getSubmission(SUBMISSION_ID);

        assertEquals("alice", detail.getUsername());
        assertEquals("两数之和", detail.getProblemTitle());
    }

    @Test
    void otherUserCannotViewSubmission() {
        UserContext.set(user(6L, "USER", "bob"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> submissionService.getSubmission(SUBMISSION_ID));

        assertEquals(ErrorCode.FORBIDDEN, ex.getErrorCode());
    }

    @Test
    void adminCanViewOthersSubmission() {
        UserContext.set(user(1L, "ADMIN", "root"));
        User owner = new User();
        owner.setId(OWNER_ID);
        owner.setUsername("alice");
        when(userMapper.selectById(OWNER_ID)).thenReturn(owner);
        stubProblem();

        SubmissionDetailVO detail = submissionService.getSubmission(SUBMISSION_ID);

        assertEquals("alice", detail.getUsername());
    }

    private UserVO user(Long id, String role, String username) {
        UserVO user = new UserVO();
        user.setId(id);
        user.setRole(role);
        user.setUsername(username);
        return user;
    }
}
