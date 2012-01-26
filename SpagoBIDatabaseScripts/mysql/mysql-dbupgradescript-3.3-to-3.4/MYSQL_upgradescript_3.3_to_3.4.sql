CREATE TABLE SBI_PROGRESS_THREAD (
       PROGRESS_THREAD_ID   INTEGER NOT NULL,
       USER_ID              VARCHAR(100) NOT NULL,
       PARTIAL              INTEGER,
       TOTAL        	      INTEGER,
       FUNCTION_CD         VARCHAR(200),
       MESSAGE              VARCHAR(4000),
       RANDOM_KEY			VARCHAR(4000),	
       PRIMARY KEY (PROGRESS_THREAD_ID)
)
 
insert into hibernate_sequences(next_val,sequence_name) values (ifnull((select max(PROGRESS_THREAD_ID)+1 from SBI_PROGRESS_THREAD) ,1),'SBI_PROGRESS_THREAD');


ALTER TABLE SBI_EXT_ROLES ADD COLUMN DO_MASSIVE_EXPORT BOOLEAN DEFAULT TRUE;