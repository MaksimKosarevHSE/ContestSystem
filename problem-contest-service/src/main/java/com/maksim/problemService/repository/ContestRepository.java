    package com.maksim.problemService.repository;

    import com.maksim.problemService.entity.Contest;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.data.jpa.repository.JpaRepository;

    public interface ContestRepository extends JpaRepository<Contest, Integer> {
        Page<Contest> findAllByOrderByStartTimeDesc(Pageable pageable);
    }
