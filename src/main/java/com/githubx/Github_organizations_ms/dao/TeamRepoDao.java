package com.githubx.Github_organizations_ms.dao;

import com.githubx.Github_organizations_ms.model.TeamRepo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeamRepoDao extends JpaRepository<TeamRepo, TeamRepo.TeamRepoId> {

    List<TeamRepo> findAllByTeamId(UUID teamId);

    Optional<TeamRepo> findByTeamIdAndRepoName(UUID teamId, String repoName);

    boolean existsByTeamIdAndRepoName(UUID teamId, String repoName);

    void deleteByTeamIdAndRepoName(UUID teamId, String repoName);

    int countByTeamId(UUID teamId);

    List<TeamRepo> findAllByFullName(String fullName);
}