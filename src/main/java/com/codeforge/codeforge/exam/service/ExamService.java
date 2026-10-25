package com.codeforge.codeforge.exam.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.codeforge.codeforge.exam.dto.ExamDetailVO;
import com.codeforge.codeforge.exam.dto.ExamRequest;
import com.codeforge.codeforge.exam.dto.ExamVO;
import com.codeforge.codeforge.exam.dto.RankItemVO;
import com.codeforge.codeforge.exam.entity.Exam;

import java.util.List;

public interface ExamService extends IService<Exam> {
    ExamVO createExam(ExamRequest request);
    ExamVO updateExam(Long id, ExamRequest request);
    void deleteExam(Long id);
    Page<ExamVO> listExams(Integer page, Integer size);
    ExamDetailVO getExamDetail(Long id);
    List<RankItemVO> getRank(Long id);
}