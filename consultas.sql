


--borrar carpeta-------------------
delete from folder where idfolder=#
#2 = select msgid from msg_folder where idfolder=#
select msgid,count(idfolder) as cuenta from msg_folder
	where msgid=#2 group by msgid
delete from msg_folder where idfolder=#
------------------------------------

--borrar mensaje carpeta------------
delete from msg_folder where msgid=#2 and folder=#
select count(*) from msg_folder where msgid=#2
--si la cuenta es > 0
delete from mensaje where msgid=#
------------------------------------
