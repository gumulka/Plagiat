# Plagiat
Tool for checking a PDF against only sources.

This is a small experiment done by me and is not intendet to be used in a professional manner.

## Usage
```
java -jar plagiat.jar [Options] pdf-File

 Option:s
  -h  --help
     Print this help message
  -v  --verbose
     Increase the verbose level
  -m  --metager
     Use Metager as a search engine
  -g  --google
     Use Google as a search enging
  -b  --begin=[page]
     The first page to check [default=1]
  -e  --end=[page]
     The last page the check [default=last page]
  -s  --source=[language]
     The language of the source file
  -t  --translate=[language]
     Translate the text into [language] and check this language to.
  -f  --file=[file]
     The file to check.
  -r  --reduceMemory
     Reduce memory usage.
  -l  --maxLinks=[number]
     Visit maximal [number] links per search result. [default=100]
  -o  --outFile=[file]
     file for printing out the result. [default=result.html]
```


## Build

Either import it in eclipse as a project or use the provided ant build file for creating the jar.

## Known Issues

At the moment, only Google and Metager are useable as search engines.
Googles blocks you out if you make to much request to it per day. This is at about 30 pages per day.

The download volume is quite high. It should only be used with a flatrate.

The results are quite unnice to read. Improvements in formatting are not my priority.

Possible languages are only german and english.

