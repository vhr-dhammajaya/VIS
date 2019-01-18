create view v_leluhur as
SELECT
        `l`.`uuid` AS `uuid`,
        `l`.`Cell_Papan_id` AS `Cell_Papan_id`,
        `l`.`nama` AS `nama`,
        `l`.`tempat_lahir` AS `tempat_lahir`,
        `l`.`tgl_lahir` AS `tgl_lahir`,
        `l`.`tempat_mati` AS `tempat_mati`,
        `l`.`tgl_mati` AS `tgl_mati`,
        `l`.`hubungan_dgn_umat` AS `hubungan_dgn_umat`,
        `l`.`active` AS `active`,
        `l`.`tgl_daftar` AS `tgl_daftar`,
        `l`.`umat_id` AS `umat_id`,
        `u`.`nama` AS `umat_nama`
    FROM
        `leluhur_smngr` `l`
        JOIN `umat` `u` ON `u`.`uuid` = `l`.`umat_id`;