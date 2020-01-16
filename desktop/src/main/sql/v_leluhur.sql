drop view v_leluhur;

create view v_leluhur as
SELECT
    l.*,
    u.nama AS umat_nama,
    case when l.cell_papan_id is null then null
        else concat(p.nama, ' ', lpad(c.row*p.width + c.col + 1, 2, '0'))
    end as lokasi_foto
FROM leluhur_smngr l
JOIN umat u ON u.uuid = l.umat_id
left join cell_papan c on c.id = l.cell_papan_id
left join papan_smngr p on p.id = c.papan_smngr_id;