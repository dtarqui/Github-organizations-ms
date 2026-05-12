package com.githubx.Github_organizations_ms;

import com.githubx.Github_organizations_ms.dao.OrgMemberDao;
import com.githubx.Github_organizations_ms.dao.OrganizationDao;
import com.githubx.Github_organizations_ms.dao.TeamDao;
import com.githubx.Github_organizations_ms.dao.TeamMemberDao;
import com.githubx.Github_organizations_ms.dao.TeamRepoDao;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
	webEnvironment = SpringBootTest.WebEnvironment.NONE,
	properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration"
	}
)
@ActiveProfiles("test")
class GithubOrganizationsMsApplicationTests {

	@MockBean
	private JwtDecoder jwtDecoder;

	@MockBean private OrganizationDao organizationDao;
	@MockBean private OrgMemberDao orgMemberDao;
	@MockBean private TeamDao teamDao;
	@MockBean private TeamMemberDao teamMemberDao;
	@MockBean private TeamRepoDao teamRepoDao;

	@Test
	void contextLoads() {
	}

}
