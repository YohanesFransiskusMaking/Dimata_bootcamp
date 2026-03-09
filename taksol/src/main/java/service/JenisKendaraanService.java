package service;

import java.util.List;

import dto.request.CreateJenisKendaraanRequest;
import dto.response.JenisKendaraanResponse;
import entity.JenisKendaraan;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import repository.JenisKendaraanRepository;

@ApplicationScoped
public class JenisKendaraanService {

    @Inject
    JenisKendaraanRepository repository;

    @Transactional
    public JenisKendaraanResponse create(CreateJenisKendaraanRequest req) {

        if (repository.findActiveByNama(req.namaJenis).isPresent()) {
            throw new WebApplicationException("namaJenis sudah terdaftar", 409);
        }

        JenisKendaraan entity = new JenisKendaraan();
        entity.setNamaJenis(req.namaJenis);
        entity.setKapasitas(req.kapasitas);
        entity.setTarifPerKm(req.tarifPerKm);
        entity.setDeskripsi(req.deskripsi);

        repository.persist(entity);

        return toResponse(entity);
    }

    private JenisKendaraanResponse toResponse(JenisKendaraan e) {
        JenisKendaraanResponse res = new JenisKendaraanResponse();
        res.id = e.getId();
        res.namaJenis = e.getNamaJenis();
        res.kapasitas = e.getKapasitas();
        res.tarifPerKm = e.getTarifPerKm();
        res.deskripsi = e.getDeskripsi();
        res.createdAt = e.getCreatedAt();
        return res;
    }

    public List<JenisKendaraanResponse> findAll() {
        return repository.listAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

}
