$version: "2"

namespace com.minigithub.organization

/// Rol de un miembro dentro de una organización
enum OrgMemberRole {
    /// Acceso total: puede gestionar miembros, equipos y repositorios
    OWNER  = "owner"
    /// Miembro regular: acceso según equipos asignados
    MEMBER = "member"
}

/// Nivel de permisos de un equipo sobre los repositorios asignados
enum TeamPermission {
    /// Solo puede leer código y abrir issues
    READ  = "read"
    /// Puede hacer push y gestionar issues y PRs
    WRITE = "write"
    /// Acceso total al repositorio (equivale a owner del repo)
    ADMIN = "admin"
}

/// Visibilidad de la organización
enum OrgVisibility {
    PUBLIC  = "public"
    PRIVATE = "private"
}
