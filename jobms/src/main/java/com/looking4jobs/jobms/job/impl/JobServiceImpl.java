package com.looking4jobs.jobms.job.impl;

import com.looking4jobs.jobms.job.Job;
import com.looking4jobs.jobms.job.JobRepository;
import com.looking4jobs.jobms.job.JobService;
import com.looking4jobs.jobms.job.dto.JobWithCompanyDTO;
import com.looking4jobs.jobms.job.external.Company;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    final
    JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobWithCompanyDTO> findAll() {

        List<Job> jobs = jobRepository.findAll();
        List<JobWithCompanyDTO> jobWithCompanyDTOS = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();

        for (Job job: jobs) {
            JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
            jobWithCompanyDTO.setJob(job);

            Company company = restTemplate.getForObject(String.format("http://localhost:8081/companies/%d", job.getCompanyId()), Company.class);
            jobWithCompanyDTO.setCompany(company);

            jobWithCompanyDTOS.add(jobWithCompanyDTO);
        }

        return jobWithCompanyDTOS;
    }

    @Override
    public void createJob(Job job) {
        jobRepository.save(job);
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteJobById(Long id) {
        try {
            jobRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateJob(Long id, Job updatedJob) {

        Optional<Job> jobOptional = jobRepository.findById(id);

        if(jobOptional.isPresent()) {

            Job job = jobOptional.get();
            job.setTitle(updatedJob.getTitle());
            job.setDescription(updatedJob.getDescription());
            job.setMinSalary(updatedJob.getMinSalary());
            job.setMaxSalary(updatedJob.getMaxSalary());
            job.setLocation(updatedJob.getLocation());
            jobRepository.save(job);
            return true;
        }

        return false;
    }
}
