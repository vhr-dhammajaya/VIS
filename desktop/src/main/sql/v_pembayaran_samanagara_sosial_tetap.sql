drop view v_pembayaran_samanagara_sosial_tetap;

create view v_pembayaran_samanagara_sosial_tetap as
select p.*,
    u.nama as umat_nama
from pembayaran_samanagara_sosial_tetap p
join umat u on u.uuid = p.umat_id;