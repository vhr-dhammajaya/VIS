drop view v_pendapatan;

create view v_pendapatan as
select
    p.*,
    u.nama as umat_nama,
    a.nama as acara_nama,
    usr.nama as user_nama
from pendapatan p
left join umat u on u.uuid = p.umat_id
left join acara a on a.id = p.acara_id
join `user` usr on usr.id = p.user_id;