--Chiara 28/08/2010
Create table `SBI_KPI_DOCUMENTS` (
	`ID_KPI_DOC` Int NOT NULL AUTO_INCREMENT,
	`BIOBJ_ID` Int NOT NULL,
	`KPI_ID` Int NOT NULL,
 Primary Key (`ID_KPI_DOC`)) ENGINE = InnoDB;
 
Alter table `SBI_KPI_DOCUMENTS` add Foreign Key (`BIOBJ_ID`) references `SBI_OBJECTS` (`BIOBJ_ID`) on delete  restrict on update  restrict;
Alter table `SBI_KPI_DOCUMENTS` add Foreign Key (`KPI_ID`) references `SBI_KPI` (`KPI_ID`) on delete  restrict on update  restrict;

INSERT INTO SBI_KPI_DOCUMENTS(KPI_ID,BIOBJ_ID)
SELECT k.KPI_ID, o.BIOBJ_ID
FROM SBI_KPI k,SBI_OBJECTS o
WHERE
k.DOCUMENT_LABEL = o.LABEL
and k.DOCUMENT_LABEL IS NOT NULL;

ALTER TABLE SBI_KPI DROP COLUMN document_label;