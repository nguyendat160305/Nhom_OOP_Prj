package controller;

import java.util.List;

import dao.ChiTietPhieuNhapDAO;
import entities.ChiTietPhieuNhap;

public class ChiTietPhieuNhapController extends ChiTietInterfaceController<ChiTietPhieuNhap, String> {

	public ChiTietPhieuNhapDAO CTPN_DAO = new ChiTietPhieuNhapDAO();

	public ChiTietPhieuNhapController() {
	}

	@Override
	public void create(List<ChiTietPhieuNhap> e) {
		CTPN_DAO.create(e);
	}

	@Override
	public void update(String id, List<ChiTietPhieuNhap> e) {
		CTPN_DAO.update(id, e);
	}

	@Override
	public void deleteById(String id) {
		CTPN_DAO.deleteById(id);
	}

	@Override
	public List<ChiTietPhieuNhap> selectAllById(String id) {
		return CTPN_DAO.selectAllById(id);
	}
}
