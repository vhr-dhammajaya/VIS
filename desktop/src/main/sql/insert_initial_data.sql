insert into user(id, username, password, nama, tipe, active) values('admin', 'admin', MD5('admin'), 'Admin', 'PENGURUS', 1);

insert into hist_biaya_smngr(id, start_date, end_date, nominal) values('0', '1990-01-01', '3000-12-31', 10000);

insert into papan_smngr(id, nama, width, height) values('A', 'A', 8, 6);
insert into papan_smngr(id, nama, width, height) values('B', 'B', 8, 6);
insert into papan_smngr(id, nama, width, height) values('C', 'C', 8, 6);
insert into papan_smngr(id, nama, width, height) values('D', 'D', 8, 6);
insert into papan_smngr(id, nama, width, height) values('E', 'E', 8, 6);
insert into papan_smngr(id, nama, width, height) values('F', 'F', 8, 6);
insert into papan_smngr(id, nama, width, height) values('G', 'G', 8, 6);
insert into papan_smngr(id, nama, width, height) values('H', 'H', 8, 6);
insert into papan_smngr(id, nama, width, height) values('I', 'I', 8, 6);
insert into papan_smngr(id, nama, width, height) values('J', 'J', 8, 6);
insert into papan_smngr(id, nama, width, height) values('K', 'K', 8, 6);
insert into papan_smngr(id, nama, width, height) values('L', 'L', 8, 6);

call insert_cells('A', 8, 6);
call insert_cells('B', 8, 6);
call insert_cells('C', 8, 6);
call insert_cells('D', 8, 6);
call insert_cells('E', 8, 6);
call insert_cells('F', 8, 6);
call insert_cells('G', 8, 6);
call insert_cells('H', 8, 6);
call insert_cells('I', 8, 6);
call insert_cells('J', 8, 6);
call insert_cells('K', 8, 6);
call insert_cells('L', 8, 6);