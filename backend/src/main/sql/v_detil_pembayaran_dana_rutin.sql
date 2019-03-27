create view v_detil_pembayaran_dana_rutin as
select d.*,
    l.nama as leluhur_nama
from detil_pembayaran_dana_rutin d
left join leluhur_smngr l on l.uuid = d.leluhur_id;