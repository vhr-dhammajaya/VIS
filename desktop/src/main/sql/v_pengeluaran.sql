drop view v_pengeluaran;

create view v_pengeluaran as
select
    p.*,
    a.nama as acara_nama,
    usr.nama as user_nama,
    concat_ws('/', 'K', extract(year from p.tgl_trx), lpad(extract(month from p.tgl_trx), 2, '0'), lpad(p.no_seq, 6, '0')) as id_trx
from pengeluaran p
left join acara a on a.id = p.acara_id
join `user` usr on usr.id = p.user_id;