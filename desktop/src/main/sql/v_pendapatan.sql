drop view v_pendapatan;

create view v_pendapatan as
select
    p.*,
    u.nama as umat_nama,
    a.nama as acara_nama,
    usr.nama as user_nama,
    concat_ws('/', 'M', 'Lain-2', extract(year from p.tgl_trx), lpad(extract(month from p.tgl_trx), 2, '0'), lpad(p.no_seq, 6, '0')) as id_trx
from pendapatan p
left join umat u on u.uuid = p.umat_id
left join acara a on a.id = p.acara_id
join `user` usr on usr.id = p.user_id;