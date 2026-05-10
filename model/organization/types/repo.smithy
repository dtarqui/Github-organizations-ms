$version: "2"

namespace com.minigithub.organization

use com.minigithub.common#RepoName
use com.minigithub.common#Uuid
use com.minigithub.common#PaginationMeta

structure OrgRepoSummary {
    @required
    id: Uuid

    @required
    name: RepoName

    @required
    fullName: String

    description: String

    @required
    starsCount: Integer

    @required
    updatedAt: String
}

list OrgRepoList {
    member: OrgRepoSummary
}

structure ListOrgReposBody {
    @required
    repositories: OrgRepoList

    @required
    pagination: PaginationMeta
}
