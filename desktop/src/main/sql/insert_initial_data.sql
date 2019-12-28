insert into user(id, username, password, nama, tipe, active) values('admin', 'admin', MD5('admin'), 'Admin', 'PENGURUS', 1);

insert into hist_biaya_smngr(id, start_date, end_date, nominal) values('0', '1990-01-01', '3000-12-31', 10000);

insert into papan_smngr(id, nama, width, height) values('A', 'A', 8, 6);

call insert_cells('A', 8, 6);