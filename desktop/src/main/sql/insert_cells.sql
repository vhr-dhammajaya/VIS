drop procedure insert_cells;

delimiter $$

create procedure insert_cells(nama_papan char(1), width int, height int)
begin
	declare i, j int default 0;

    while i < height do
        set j = 0; -- IMPORTANT!

		while j < width do
			insert into cell_papan values(concat(nama_papan, ' ', i, ' ', j), nama_papan, null, i, j);
			set j = j+1;
        end while;

        set i = i+1;
    end while;
end;

$$