import sys, getopt,os,ntpath, argparse
from pathlib import Path

mainpathList=[]
mainStart=""
curentEnd=""
def splitData(curPath, numberOfSplits,primary):
   pathList=[]
   userPathList=[]
   global mainStart
   global curentEnd
   global mainpathList
   if primary==True:
       mainStart=curPath
       for user in range(0,numberOfSplits):       
           pathList.append(curPath+"_"+str(user))
           mainpathList.append(curPath+"_"+str(user))
   else:
       curentEnd=curPath.replace(mainStart,"")
       for userPath in  mainpathList:   
           subFolderName=os.path.basename(os.path.normpath(curPath))
           pathList.append(userPath+curentEnd)
   print (curPath)   
   onlyfiles = next(os.walk(curPath))[2]    
   numberOfFilesProUser=int(len(onlyfiles)/len(pathList))
   for pathListElement in pathList:
       currentNumberOfFiles=numberOfFilesProUser
       os.makedirs(pathListElement,511,True)
       
       for Filename in onlyfiles:
           if (currentNumberOfFiles>0):
               for SameFilename in onlyfiles:            
                    if (currentNumberOfFiles<1 and (Filename.split('.')[0]==SameFilename.split('.')[0]) and (Filename.split('.')[1]!=SameFilename.split('.')[1])) or((Filename.split('.')[0]==SameFilename.split('.')[0]) and (Filename.split('.')[1]!=SameFilename.split('.')[1])):
                        if Path(curPath+"\\"+Filename).exists(): 
                            os.replace(curPath+"\\"+Filename, pathListElement+"\\"+Filename)
                            onlyfiles = next(os.walk(curPath))[2]
                            currentNumberOfFiles=currentNumberOfFiles-1 
                        os.replace(curPath+"\\"+SameFilename, pathListElement+"\\"+SameFilename)
                        onlyfiles = next(os.walk(curPath))[2]
                        currentNumberOfFiles=currentNumberOfFiles-1
               
                    
   subfolders= [f.path for f in os.scandir(curPath) if f.is_dir()]
   if(len(subfolders)>0):
       for subPath in list(subfolders):
           
           splitData(subPath, numberOfSplits,False)


   
parser = argparse.ArgumentParser()
requiredArguments = parser.add_argument_group("required arguments")
requiredArguments.add_argument("-s", "--sourcedir", type=str, required=True, help="Directory containing the annotations and images")
requiredArguments.add_argument("-n", "--numberofsplits", type=int, required=True, help="The number of Splits - for each split a seperate directory will be created")
    

args = parser.parse_args()
if (not os.path.exists(args.sourcedir) or not os.path.isdir(args.sourcedir)):
    sys.exit("Source directory does not exists or is not a directory") 
mainPath =  args.sourcedir
numberOfSplits = args.numberofsplits
   
print ('Main path : "', mainPath)
print ('Number of splits : "', numberOfSplits)
  
splitData(mainPath,numberOfSplits,True)
print ('Done')



       