drop view v_leluhur;

create view v_leluhur as
SELECT
    l.*,
    u.nama AS umat_nama
FROM leluhur_smngr l
JOIN umat u ON u.uuid = l.umat_id;