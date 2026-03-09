package repository;

import entity.Role;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<Role, Integer> {

    public Role findByRoleName(String role) {
        return find("role", role).firstResult();
    }

    public boolean existsByName(String role) {
        return count("role", role) > 0;
    }

}
