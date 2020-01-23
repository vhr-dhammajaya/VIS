drop view v_pengeluaran;

create view v_pengeluaran as
select
    p.*,
    a.nama as acara_nama,
    usr.nama as user_nama
from pengeluaran p
left join acara a on a.id = p.acara_id
join `user` usr on usr.id = p.user_id;