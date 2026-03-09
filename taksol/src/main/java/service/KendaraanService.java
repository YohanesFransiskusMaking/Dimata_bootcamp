package service;

import java.util.List;

import org.eclipse.microprofile.jwt.JsonWebToken;

import dto.request.CreateKendaraanRequest;
import dto.request.UpdateKendaraanRequest;
import dto.request.UpdateStatusRequest;
import dto.response.KendaraanResponse;
import entity.AppUser;
import entity.JenisKendaraan;
import entity.Kendaraan;
import entity.KendaraanStatus;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import mapper.KendaraanMapper;
import repository.AppUserRepository;
import repository.JenisKendaraanRepository;
import repository.KendaraanRepository;
import security.SecurityUtil;

@ApplicationScoped
public class KendaraanService {

    @Inject
    KendaraanRepository kendaraanRepository;

    @Inject
    JsonWebToken jwt;

    @Inject
    JenisKendaraanRepository jenisKendaraanRepository;

    @Inject
    AppUserRepository userRepository;

    @Inject
    KendaraanMapper kendaraanMapper;
    @Inject
    SecurityUtil securityUtil;

    @Transactional
    public KendaraanResponse createKendaraan(CreateKendaraanRequest request) {

        // 🔐 Ambil userId dari JWT (AMAN)
        Long userId = securityUtil.getCurrentUserId();

        AppUser driver = userRepository.findById(userId);

        if (driver == null) {
            throw new WebApplicationException("User tidak ditemukan", 404);
        }

        if (!driver.hasRole("DRIVER")) {
            throw new WebApplicationException("User bukan driver", 403);
        }

        if (kendaraanRepository.findByDriver(driver).isPresent()) {
            throw new WebApplicationException("Driver sudah memiliki kendaraan", 400);
        }

        JenisKendaraan jenis = jenisKendaraanRepository.findById(request.getJenisKendaraanId());
        if (jenis == null) {
            throw new WebApplicationException("Jenis kendaraan tidak ditemukan", 404);
        }

        Kendaraan kendaraan = new Kendaraan();
        kendaraan.setPlatNomor(request.getPlatNomor());
        kendaraan.setJenisKendaraan(jenis);
        kendaraan.setDriver(driver);
        kendaraan.setStatus(KendaraanStatus.ACTIVE);

        kendaraanRepository.persist(kendaraan);

        return kendaraanMapper.toResponse(kendaraan);
    }

    @Transactional
public KendaraanResponse getMyVehicle() {

    Long userId = securityUtil.getCurrentUserId();

    AppUser driver = userRepository.findById(userId);

    return kendaraanRepository.findByDriver(driver)
            .map(kendaraanMapper::toResponse)
            .orElseThrow(() ->
                    new WebApplicationException("Kendaraan tidak ditemukan", 404));
}

@Transactional
public KendaraanResponse updateMyVehicle(UpdateKendaraanRequest request) {

    Long userId = securityUtil.getCurrentUserId();

    AppUser driver = userRepository.findById(userId);

    Kendaraan kendaraan = kendaraanRepository.findByDriver(driver)
            .orElseThrow(() ->
                    new WebApplicationException("Kendaraan tidak ditemukan", 404));

    kendaraan.setPlatNomor(request.getPlatNomor());

    return kendaraanMapper.toResponse(kendaraan);
}


@Transactional
public KendaraanResponse updateStatus(UpdateStatusRequest request) {

    Long userId = securityUtil.getCurrentUserId();

    AppUser driver = userRepository.findById(userId);

    Kendaraan kendaraan = kendaraanRepository.findByDriver(driver)
            .orElseThrow(() ->
                    new WebApplicationException("Kendaraan tidak ditemukan", 404));

    kendaraan.setStatus(request.getStatus());

    return kendaraanMapper.toResponse(kendaraan);
}


@Transactional
public void deleteMyVehicle() {

    Long userId = securityUtil.getCurrentUserId();

    AppUser driver = userRepository.findById(userId);

    Kendaraan kendaraan = kendaraanRepository.findByDriver(driver)
            .orElseThrow(() ->
                    new WebApplicationException("Kendaraan tidak ditemukan", 404));

    kendaraanRepository.delete(kendaraan);
}


public List<KendaraanResponse> getAll() {
    return kendaraanRepository.listAll()
            .stream()
            .map(kendaraanMapper::toResponse)
            .toList();
}


public KendaraanResponse getById(Long id) {

    Kendaraan kendaraan = kendaraanRepository.findById(id);

    if (kendaraan == null) {
        throw new WebApplicationException("Kendaraan tidak ditemukan", 404);
    }

    return kendaraanMapper.toResponse(kendaraan);
}


@Transactional
public KendaraanResponse updateStatusByAdmin(Long id, UpdateStatusRequest request) {

    Kendaraan kendaraan = kendaraanRepository.findById(id);

    if (kendaraan == null) {
        throw new WebApplicationException("Kendaraan tidak ditemukan", 404);
    }

    kendaraan.setStatus(request.getStatus());

    return kendaraanMapper.toResponse(kendaraan);
}


}
