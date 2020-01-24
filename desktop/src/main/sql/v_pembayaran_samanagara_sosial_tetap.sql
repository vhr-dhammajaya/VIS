drop view v_pembayaran_samanagara_sosial_tetap;

create view v_pembayaran_samanagara_sosial_tetap as
select p.*,
    u.nama as umat_nama,
    usr.nama as user_nama,
    concat_ws('/', 'M', case p.tipe when 'samanagara' then 'Foto' else 'SosTetap' end,
        extract(year from p.tgl), lpad(extract(month from p.tgl), 2, '0'),
        lpad(p.no_seq, 6, '0')
    ) as id_trx
from pembayaran_samanagara_sosial_tetap p
join umat u on u.uuid = p.umat_id
join `user` usr on usr.id = p.user_id;