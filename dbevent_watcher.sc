#include <stdio.h>
#include <stdlib.h>

  EXEC SQL include sqlca;

int main(int,char **);

int main(int argc,char *argv[])
{
  EXEC SQL BEGIN DECLARE SECTION;
  char *db;
  char username[32];
  char dbname[32];
  char evnam[33],evtxt[2000],evtime[26];
  char ev[100];
  EXEC SQL END DECLARE SECTION;
  int evcnt=0;
  
  if(argc<2)
  {
    printf("Usage: %s <dbname>\n",argv[0]);
    exit(1);
  }
  db=argv[1];

  EXEC sql connect :db;
  if (sqlca.sqlcode<0)
  {
    printf("%d No connect\n%s\n",sqlca.sqlcode,sqlca.sqlerrm.sqlerrmc);
    exit(2);
  }
  EXEC SQL select user,dbmsinfo('database') into :username,:dbname;
  if(sqlca.sqlcode<0)printf("%s\n",sqlca.sqlerrm.sqlerrmc);
  else printf("User %s connected to %s\n",username,dbname);
  EXEC SQL set_sql (savequery=1);
  EXEC sql declare cs cursor for 
     select distinct 'register dbevent ' + squeeze(event_name) 
       from iievents where lowercase(event_name) like 'dd\_%' escape '\';
  EXEC SQL open cs for readonly;
  while(1)
  { 
    EXEC SQL fetch cs into :ev;
    if(sqlca.sqlcode<0)
    {
       fprintf(stderr,"%s\n",sqlca.sqlerrm.sqlerrmc);
       EXEC SQL disconnect;
       exit(1);
    }
    else if(sqlca.sqlcode==100) break;
    evcnt++;
    fprintf(stderr,"<%s>\n",ev);
    EXEC SQL execute immediate :ev;
    if(sqlca.sqlcode<0)fprintf(stderr,"%s\n",sqlca.sqlerrm.sqlerrmc);
  }
  if(!evcnt)
  {
     fprintf(stderr,"did not find any event!\n");
     EXEC SQL disconnect;
     exit(1);
  }
  EXEC SQL commit;
  EXEC SQL register dbevent stopwatch;
  if(sqlca.sqlcode<0)
  {
    fprintf(stderr,"If the dbevent \"stopwatch\" were created,\nyou could ");
    fprintf(stderr,"stop this program by raising this event.\n");
    fprintf(stderr,"It isn\'t, so please stop with ^C\n");
  }
  else fprintf(stderr,"Stop this program by raising event \"stopwatch\"\n");
  fprintf(stderr,"Waiting for replicator dbevents...\n");
  while(1)
  {
    EXEC SQL get dbevent with wait;
    if(sqlca.sqlcode<0)break;
    {
       EXEC SQL inquire_sql(:evnam=dbeventname,:evtxt=dbeventtext,
                            :evtime=dbeventtime);
       if(sqlca.sqlcode<0)break;
       printf("%s <%s> <%s>\n",evtime,evnam,evtxt);
       if(!strcmp(evnam,"stopwatch"))break;
    }
  }

  EXEC SQL disconnect;
  return 0;
}

