#The Link To the Performance Analysis report 
https://docs.google.com/document/d/19y2QSoPQLgJ-x0ty24qc1n8n88J-Ee7XqoIEm99Qnk0/edit#heading=h.v6qgz1or1nl7

#Link of the database
	https://drive.google.com/file/d/1K6dAGxjIHPpVVquQA4xv2ePqtW_e8bC1/view?usp=sharing

#Link of the JARs
	https://drive.google.com/drive/folders/1xQQFibiF9A3Q0cd7XtLb_mZR_tnYc21p?usp=sharing

#Steps to run the project
	1- Clone the repository
	2- Open the project in IntelliJ
	3- Wait for it finish indexing
	4- Enable auto-import in Maven
	5- Click File->Project Structure->Modules->Dependencies
	6- Delete any Dependencies that do not start with "Maven" or "Hibernate"
	7- Add the jar files (check the link in the README.md in the repo) in these dependencies 
	(some files will ask you to choose a category from a list, choose Jar Directory)
	by clicking the '+' sign at the top left then JARs or directories...
	8- Add the folder (check the link in the README.md in the repo) in these dependencies
	by clicking the '+' sign at the top left then JARs or directories...
	9- Right click on the project name and select Maven-> Download Sources and Documentation
	10- Search-Engine->src->main->webapp->reactjs and open a terminal in this directory
	11- Type "npm install"
	12- Search-Engine->src->main->webapp->reactjs->JsonServer and open a terminal in this directory
	13- Type "npm install"
	14- Type "npm run json:server"
	15- Wait for the server to run
	15- Search-Engine->src->main->webapp->reactjs and open a terminal in this directory
	16- Type "npm start"
	17- It will ask you to run on a different port, press 'y'
	18- IntelliJ should start indexing agian, wait for it to finish
	19- Download Xampp and Start Apache and MySQL
	20- There is an already made database, to use it, download it (check the link in the README.md in the repo)
	if you do not want to use it, you can go directly to step 24, if you will use it, then skip step
	21- Go to the directory where the database was downloaded and copy all the files and folders EXCEPT
	share, bin and script
	22- Go to xampp->mysql
	23- Paste and replace(do not include "bin" "scripts" "share"  in transfering the files to your mysql file)
	Follow Step 24 if you want to create the database on your own.If you are willing to run ours just go to step 25 right away
	24- Run WebCrawler->Indexer->ForPopularity->Populairty->ImageIndexer->FinalUrls
	25- Search-Engine->src->main->java->com.mightyjava->Right click on Application and click "Run 'Application.main()'"

