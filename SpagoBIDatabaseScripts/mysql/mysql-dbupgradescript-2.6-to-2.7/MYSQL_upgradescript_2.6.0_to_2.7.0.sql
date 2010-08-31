--Chiara 28/08/2010
ALTER TABLE SBI_KPI DROP COLUMN document_label;

Create table `SBI_KPI_DOCUMENTS` (
	`ID_KPI_DOC` Int NOT NULL AUTO_INCREMENT,
	`BIOBJ_ID` Int NOT NULL,
	`KPI_ID` Int NOT NULL,
 Primary Key (`ID_KPI_DOC`)) ENGINE = InnoDB;
 
Alter table `SBI_KPI_DOCUMENTS` add Foreign Key (`BIOBJ_ID`) references `SBI_OBJECTS` (`BIOBJ_ID`) on delete  restrict on update  restrict;
Alter table `SBI_KPI_DOCUMENTS` add Foreign Key (`KPI_ID`) references `SBI_KPI` (`KPI_ID`) on delete  restrict on update  restrict;