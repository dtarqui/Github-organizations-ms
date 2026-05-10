package com.githubx.Github_organizations_ms.grpc;

import com.githubx.Github_organizations_ms.service.contratos.OrganizationService;
import com.githubx.Github_organizations_ms.service.contratos.TeamRepoService;
import com.githubx.Github_organizations_ms.util.errorhandling.EntityNotFoundException;
import com.minigithub.grpc.proto.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@RequiredArgsConstructor
public class GrpcOrgPublicServiceImpl extends OrgPublicServiceGrpc.OrgPublicServiceImplBase {

    private final OrganizationService organizationService;
    private final TeamRepoService teamRepoService;
    private final GrpcProtoMapper mapper;

    @Override
    public void getOrganization(GetOrganizationRequest req,
                                StreamObserver<GetOrganizationResponse> obs) {
        try {
            obs.onNext(GetOrganizationResponse.newBuilder()
                    .setOrganization(mapper.toProtoOrg(organizationService.getOrganization(req.getOrgName())))
                    .build());
            obs.onCompleted();
        } catch (EntityNotFoundException e) {
            obs.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void listOrgRepos(ListOrgReposRequest req,
                             StreamObserver<ListOrgReposResponse> obs) {
        try {
            int page = req.getPagination().getPage() > 0 ? req.getPagination().getPage() : 1;
            int perPage = req.getPagination().getPerPage() > 0 ? req.getPagination().getPerPage() : 10;
            var result = teamRepoService.listOrgRepos(req.getOrgName(), page, perPage);
            obs.onNext(ListOrgReposResponse.newBuilder()
                    .addAllRepositories(result.getRepositories().stream().map(mapper::toProtoRepoSummary).toList())
                    .setPagination(mapper.toProtoPagination(result.getPagination()))
                    .build());
            obs.onCompleted();
        } catch (EntityNotFoundException e) {
            obs.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            obs.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }
}
