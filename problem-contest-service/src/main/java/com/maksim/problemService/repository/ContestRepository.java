    package com.maksim.problemService.repository;


    import com.maksim.problemService.dto.contest.ContestSignatureDto;
    import com.maksim.problemService.entity.Contest;
    import com.maksim.problemService.entity.Problem;
    import com.maksim.problemService.entity.ProblemConstraints;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface ContestRepository extends JpaRepository<Contest, Integer> {

        @Query("select cp.problem from ContestProblem cp " +
                "where cp.contest.id = :contestId and cp.problem.id = :problemId")
        Optional<Problem> getProblem(Integer contestId, Integer problemId);


        @Query("select new com.maksim.problemService.dto.ContestSignatureDto(c.id, c.authorId, c.startTime, c.endTime) from Contest c order by c.startTime desc")
        List<ContestSignatureDto> getAll(PageRequest of);

        @Query("select new com.maksim.problemService.dto.ContestSignatureDto(cu.contest.id, cu.contest.authorId, cu.contest.startTime, cu.contest.endTime) from ContestUser cu where cu.id.userId = :userId order by cu.contest.startTime desc")
        List<ContestSignatureDto> getUserContests(int userId, PageRequest of);

        @Query("select count(*) from Problem p where p.creatorId = :authorId and p.isPublic = false and p.id in (:uniqueIds)")
        int getAuthorProblemsListCount(int authorId, ArrayList<Integer> uniqueIds);

        @Query("select new com.maksim.problemService.entity.ProblemConstraints(cp.problem.id, cp.problem.compileTimeLimit, cp.problem.timeLimit, cp.problem.memoryLimit, cp.contest.id, cp.contest.startTime, cp.contest.endTime) from ContestProblem cp where cp.contest.id= :contestId and cp.problem.id = :problemId")
        Optional<ProblemConstraints> getProblemConstraints(Integer contestId, Integer problemId);
    }
