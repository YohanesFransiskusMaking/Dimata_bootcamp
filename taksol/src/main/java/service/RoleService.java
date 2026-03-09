package service;

import dto.request.RoleRequest;
import dto.response.RoleResponse;
import entity.Role;
import exception.ConflictException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import repository.RoleRepository;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class RoleService {

    @Inject
    RoleRepository repository;

    @Transactional
    public RoleResponse create(RoleRequest request) {

        if (repository.existsByName(request.role)) {
            throw new ConflictException("Role sudah ada");
        }

        Role role = new Role();
        role.setRole(request.role.toUpperCase());

        repository.persist(role);

        return new RoleResponse(role.getId(), role.getRole());
    }

    public List<RoleResponse> findAll() {
        return repository.listAll()
                .stream()
                .map(r -> new RoleResponse(r.getId(), r.getRole()))
                .collect(Collectors.toList());
    }

    public RoleResponse findById(Integer id) {
        Role role = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));

        return new RoleResponse(role.getId(), role.getRole());
    }

    @Transactional
    public RoleResponse update(Integer id, RoleRequest request) {

        Role role = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));

        if (!role.getRole().equals(request.role)
                && repository.existsByName(request.role)) {
            throw new ConflictException("Role sudah ada");
        }

        role.setRole(request.role.toUpperCase());

        return new RoleResponse(role.getId(), role.getRole());
    }

    @Transactional
    public void delete(Integer id) {

        Role role = repository.findByIdOptional(id)
                .orElseThrow(() -> new NotFoundException("Role tidak ditemukan"));

        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new ConflictException("Role masih digunakan oleh user");
        }

        repository.delete(role);
    }
}
