package com.githubx.Github_organizations_ms.grpc;

import com.githubx.Github_organizations_ms.generated.model.AddOrgMemberBody;
import com.githubx.Github_organizations_ms.generated.model.AddTeamRepoBody;
import com.githubx.Github_organizations_ms.generated.model.CreateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.CreateTeamBody;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrgMemberRoleBody;
import com.githubx.Github_organizations_ms.generated.model.UpdateOrganizationBody;
import com.githubx.Github_organizations_ms.generated.model.UpdateTeamBody;
import com.githubx.Github_organizations_ms.service.contratos.OrgMemberService;
import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import com.githubx.Github_organizations_ms.service.contratos.TeamMemberService;
import com.githubx.Github_organizations_ms.service.contratos.TeamRepoService;
import com.githubx.Github_organizations_ms.service.contratos.TeamService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityConflictException;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.githubx.Github_organizations_ms.util.errorhandling.ForbiddenOperationException;
import com.minigithub.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcOrgServiceImpl extends OrgServiceGrpc.OrgServiceImplBase {

    private final OrganizationService organizationService;
    private final OrgMemberService orgMemberService;
    private final TeamService teamService;
    private final TeamMemberService teamMemberService;
    private final TeamRepoService teamRepoService;
    private final GrpcProtoMapper mapper;

    // ─── Organizations ────────────────────────────────────────

    @Override
    public void listMyOrganizations(ListMyOrganizationsRequest req,
                                    StreamObserver<ListMyOrganizationsResponse> obs) {
        try {
            int page = req.getPagination().getPage() > 0 ? req.getPagination().getPage() : 1;
            int perPage = req.getPagination().getPerPage() > 0 ? req.getPagination().getPerPage() : 10;
            var result = organizationService.listMyOrganizations(page, perPage);
            obs.onNext(ListMyOrganizationsResponse.newBuilder()
                    .addAllOrganizations(result.getOrganizations().stream().map(mapper::toProtoOrg).toList())
                    .setPagination(mapper.toProtoPagination(result.getPagination()))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void createOrganization(CreateOrganizationRequest req,
                                   StreamObserver<CreateOrganizationResponse> obs) {
        try {
            var body = new CreateOrganizationBody()
                    .name(req.getName())
                    .displayName(req.getDisplayName())
                    .description(req.getDescription())
                    .website(req.getWebsite())
                    .visibility(mapper.fromProtoVisibility(req.getVisibility()));
            obs.onNext(CreateOrganizationResponse.newBuilder()
                    .setOrganization(mapper.toProtoOrg(organizationService.createOrganization(body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void updateOrganization(UpdateOrganizationRequest req,
                                   StreamObserver<UpdateOrganizationResponse> obs) {
        try {
            var body = new UpdateOrganizationBody()
                    .displayName(req.getDisplayName().isEmpty() ? null : req.getDisplayName())
                    .description(req.getDescription().isEmpty() ? null : req.getDescription())
                    .website(req.getWebsite().isEmpty() ? null : req.getWebsite())
                    .avatarUrl(req.getAvatarUrl().isEmpty() ? null : req.getAvatarUrl());
            if (req.getVisibility() != OrgVisibility.ORG_VISIBILITY_UNSPECIFIED) {
                body.setVisibility(mapper.fromProtoVisibility(req.getVisibility()));
            }
            obs.onNext(UpdateOrganizationResponse.newBuilder()
                    .setOrganization(mapper.toProtoOrg(organizationService.updateOrganization(req.getOrgName(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void deleteOrganization(DeleteOrganizationRequest req,
                                   StreamObserver<DeleteOrganizationResponse> obs) {
        try {
            organizationService.deleteOrganization(req.getOrgName());
            obs.onNext(DeleteOrganizationResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Org Members ──────────────────────────────────────────

    @Override
    public void listOrgMembers(ListOrgMembersRequest req,
                               StreamObserver<ListOrgMembersResponse> obs) {
        try {
            var result = orgMemberService.listOrgMembers(req.getOrgName());
            obs.onNext(ListOrgMembersResponse.newBuilder()
                    .addAllMembers(result.getMembers().stream().map(mapper::toProtoMember).toList())
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void addOrgMember(AddOrgMemberRequest req,
                             StreamObserver<AddOrgMemberResponse> obs) {
        try {
            var body = new AddOrgMemberBody()
                    .username(req.getUsername())
                    .role(mapper.fromProtoMemberRole(req.getRole()));
            obs.onNext(AddOrgMemberResponse.newBuilder()
                    .setMember(mapper.toProtoMember(orgMemberService.addOrgMember(req.getOrgName(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void updateOrgMemberRole(UpdateOrgMemberRoleRequest req,
                                    StreamObserver<UpdateOrgMemberRoleResponse> obs) {
        try {
            var body = new UpdateOrgMemberRoleBody().role(mapper.fromProtoMemberRole(req.getRole()));
            obs.onNext(UpdateOrgMemberRoleResponse.newBuilder()
                    .setMember(mapper.toProtoMember(
                            orgMemberService.updateOrgMemberRole(req.getOrgName(), req.getUsername(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void removeOrgMember(RemoveOrgMemberRequest req,
                                StreamObserver<RemoveOrgMemberResponse> obs) {
        try {
            orgMemberService.removeOrgMember(req.getOrgName(), req.getUsername());
            obs.onNext(RemoveOrgMemberResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Teams ────────────────────────────────────────────────

    @Override
    public void listOrgTeams(ListOrgTeamsRequest req, StreamObserver<ListOrgTeamsResponse> obs) {
        try {
            var result = teamService.listOrgTeams(req.getOrgName());
            obs.onNext(ListOrgTeamsResponse.newBuilder()
                    .addAllTeams(result.getTeams().stream().map(mapper::toProtoTeam).toList())
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void createTeam(CreateTeamRequest req, StreamObserver<CreateTeamResponse> obs) {
        try {
            var body = new CreateTeamBody()
                    .name(req.getName())
                    .description(req.getDescription())
                    .permission(mapper.fromProtoPermission(req.getPermission()));
            obs.onNext(CreateTeamResponse.newBuilder()
                    .setTeam(mapper.toProtoTeam(teamService.createTeam(req.getOrgName(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void getTeam(GetTeamRequest req, StreamObserver<GetTeamResponse> obs) {
        try {
            obs.onNext(GetTeamResponse.newBuilder()
                    .setTeam(mapper.toProtoTeam(teamService.getTeam(req.getOrgName(), req.getTeamId())))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void updateTeam(UpdateTeamRequest req, StreamObserver<UpdateTeamResponse> obs) {
        try {
            var body = new UpdateTeamBody()
                    .name(req.getName().isEmpty() ? null : req.getName())
                    .description(req.getDescription().isEmpty() ? null : req.getDescription());
            if (req.getPermission() != TeamPermission.TEAM_PERMISSION_UNSPECIFIED) {
                body.setPermission(mapper.fromProtoPermission(req.getPermission()));
            }
            obs.onNext(UpdateTeamResponse.newBuilder()
                    .setTeam(mapper.toProtoTeam(teamService.updateTeam(req.getOrgName(), req.getTeamId(), body)))
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void deleteTeam(DeleteTeamRequest req, StreamObserver<DeleteTeamResponse> obs) {
        try {
            teamService.deleteTeam(req.getOrgName(), req.getTeamId());
            obs.onNext(DeleteTeamResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Team Members ─────────────────────────────────────────

    @Override
    public void listTeamMembers(ListTeamMembersRequest req,
                                StreamObserver<ListTeamMembersResponse> obs) {
        try {
            var result = teamMemberService.listTeamMembers(req.getOrgName(), req.getTeamId());
            obs.onNext(ListTeamMembersResponse.newBuilder()
                    .addAllMembers(result.getMembers().stream().map(mapper::toProtoTeamMember).toList())
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void addTeamMember(AddTeamMemberRequest req, StreamObserver<AddTeamMemberResponse> obs) {
        try {
            teamMemberService.addTeamMember(req.getOrgName(), req.getTeamId(), req.getUsername());
            obs.onNext(AddTeamMemberResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void removeTeamMember(RemoveTeamMemberRequest req,
                                 StreamObserver<RemoveTeamMemberResponse> obs) {
        try {
            teamMemberService.removeTeamMember(req.getOrgName(), req.getTeamId(), req.getUsername());
            obs.onNext(RemoveTeamMemberResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Team Repos ───────────────────────────────────────────

    @Override
    public void listTeamRepos(ListTeamReposRequest req, StreamObserver<ListTeamReposResponse> obs) {
        try {
            var result = teamRepoService.listTeamRepos(req.getOrgName(), req.getTeamId());
            obs.onNext(ListTeamReposResponse.newBuilder()
                    .addAllRepos(result.getRepos().stream().map(mapper::toProtoTeamRepo).toList())
                    .build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void addTeamRepo(AddTeamRepoRequest req, StreamObserver<AddTeamRepoResponse> obs) {
        try {
            var body = new AddTeamRepoBody().permission(mapper.fromProtoPermission(req.getPermission()));
            teamRepoService.addTeamRepo(req.getOrgName(), req.getTeamId(), req.getRepoName(), body);
            obs.onNext(AddTeamRepoResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    @Override
    public void removeTeamRepo(RemoveTeamRepoRequest req, StreamObserver<RemoveTeamRepoResponse> obs) {
        try {
            teamRepoService.removeTeamRepo(req.getOrgName(), req.getTeamId(), req.getRepoName());
            obs.onNext(RemoveTeamRepoResponse.newBuilder().setSuccess(true).build());
            obs.onCompleted();
        } catch (Exception e) {
            obs.onError(toStatus(e).asRuntimeException());
        }
    }

    // ─── Exception mapping ────────────────────────────────────

    private Status toStatus(Exception e) {
        if (e instanceof EntityNotFoundException) return Status.NOT_FOUND.withDescription(e.getMessage());
        if (e instanceof EntityConflictException) return Status.ALREADY_EXISTS.withDescription(e.getMessage());
        if (e instanceof ForbiddenOperationException) return Status.PERMISSION_DENIED.withDescription(e.getMessage());
        return Status.INTERNAL.withDescription(e.getMessage());
    }
}
