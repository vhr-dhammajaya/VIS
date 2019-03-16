drop view v_pembayaran_dana_rutin;

create view v_pembayaran_dana_rutin as
select p.*,
    extract(year from ut_thn_bln) as ut_thn,
    extract(month from ut_thn_bln) as ut_bln,
    u.nama as umat_nama, l.nama as leluhur_nama
from pembayaran_dana_rutin p
join umat u on u.uuid = p.umat_id
left join leluhur_smngr l on l.uuid = p.leluhur_id;