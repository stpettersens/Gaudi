# Gaudi installation script (NSIS) for Windows
# Auto-generated in part by EclipseNSIS Script Wizard
#
# This script uses EnvVarUpdate.nsh from:
# http://nsis.sourceforge.net/Environmental_Variables:_append,_prepend,_and_remove_entries
#
# This script also uses the NSISArray plug-in from:
# http://nsis.sourceforge.net/Arrays_in_NSIS

Name Gaudi

# General Symbol Definitions
!define REGKEY "SOFTWARE\$(^Name)"
!define VERSION 0.1.0.0
!define COMPANY "Sam Saint-Pettersen"
!define URL github.com/stpettersens/Gaudi
!define DESC "Gaudi platform agnostic build tool"
!define COPYRIGHT "(c) 2010 Sam Saint-Pettersen"

# MUI Symbol Definitions
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\orange-install.ico"
!define MUI_FINISHPAGE_NOAUTOCLOSE
!define MUI_LICENSEPAGE_CHECKBOX
!define MUI_STARTMENUPAGE_REGISTRY_ROOT HKLM
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_REGISTRY_KEY ${REGKEY}
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME StartMenuGroup
!define MUI_STARTMENUPAGE_DEFAULTFOLDER Gaudi
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\orange-uninstall.ico"
!define MUI_UNFINISHPAGE_NOAUTOCLOSE

# Included files
!include Sections.nsh
!include MUI2.nsh
!include LogicLib.nsh
; " " Quoted includes do not ship with NSIS by default
!include "EnvVarUpdate.nsh" 
!include "NSISArray.nsh"

# Variables
Var StartMenuGroup
Var lib
Var libsFound
Var indx

# Installer pages
;!insertmacro MUI_PAGE_WELCOME
;!insertmacro MUI_PAGE_LICENSE license.txt
!insertmacro MUI_PAGE_COMPONENTS
;!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuGroup
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

# Installer languages
!insertmacro MUI_LANGUAGE English

# Installer attributes
OutFile Gaudi_setup.exe
InstallDir $PROGRAMFILES\Gaudi
CRCCheck on
XPStyle on
ShowInstDetails show
VIProductVersion 0.1.0.0
VIAddVersionKey ProductName Gaudi
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey CompanyName "${COMPANY}"
VIAddVersionKey CompanyWebsite "${URL}"
VIAddVersionKey FileVersion "${VERSION}"
VIAddVersionKey FileDescription "${DESC}"
VIAddVersionKey LegalCopyright "${COPYRIGHT}"
InstallDirRegKey HKLM "${REGKEY}" Path
ShowUninstDetails show

# Create libraries ("libs") arrays and array functions for use later
# <BEGIN>
${Array} libs 1 20
${ArrayFunc} Read
${ArrayFunc} Write
${ArrayFunc} FreeUnusedMem

${Array} libsRedun 1 20
${ArrayFunc} Read
${ArrayFunc} Push
${ArrayFunc} FreeUnusedMem

Section
	${libs->Init}
	${libsRedun->Init}
	${libs->Write} 0 "scala-library.jar"
	${libs->Write} 1 "json_simple-1.1.jar"
	${libs->Write} 2 "commons-io-1.4.jar"
	${libs->FreeUnusedMem}
	${libsRedun->FreeUnusedMem}
SectionEnd
# <END>

# Detect presence of a suitable JVM environment on system
# That is, that it exists and is at least version 1.5+ capable
Function detectJVM
	SetOutPath .
	File JavaCheck.class ; Extract small Java version checker program
	; Attempt to execute Java version checker program to get version
	; Also does 'java' even exist?
	nsExec::ExecToStack `java -classpath . JavaCheck 1.5` 
	Pop $0 ; Pop return code from program from stack
	Pop $1 ; Pop stdout from program from stack
	${If} $0 == "error" ; Error occurs when a JVM cannot be found...
		DetailPrint "No JVM detected!"
		${If} ${Cmd} `MessageBox MB_YESNO|MB_ICONQUESTION "No JVM was detected. Download one now?" IDYES`
		downloadJVM:
		; Go to download site for Java; possibly change to list of Gaudi-compatible JVMs
		DetailPrint "Opening Java download page in your web browser."
		ExecShell "open" "http://www.java.com/download" 
		GoTo badJVM
		${Else}
			GoTo badJVM
		${EndIf}
	${EndIf}
	;Delete JavaCheck.class ; Done with this program, delete it
	DetailPrint "Detected JVM: version $1" ; Display detected version 
	# Check this JVM meets minimum version requirement (v1.5.x)
	${If} $0 == "1"
		DetailPrint "JVM reports suitable version (1.5+)"
		GoTo goodJVM
	${ElseIf} $0 == "0"
		DetailPrint "JVM reports unsuitable version (< 1.5)"
		DetailPrint "Please update it."
		GoTo downloadJVM
		badJVM: ; Done with JVM check; failed
		DetailPrint "JVM requirement was not met, so installation was aborted."
		DetailPrint "Please download and/or install a suitable JVM and run this setup again."
		Abort
		goodJVM: ; Done with JVM check; passed
	${EndIf}
FunctionEnd

# Detect if third-party libraries Gaudi requires are
# present on system by looking in the system's CLASSPATH
Function detectTPLibs
	File FindInPath.class ; Extract small FindInPath program
	IntOp $libsFound $libsFound + 0 ; Set libraries found to 0
	IntOp $indx $indx + 0 ; Set loop index to 0
	${DoUntil} $indx == 3
		${libs->Read} $lib $indx ; Read indexed library as current library  to check for
		; Execute FindInPath program to check for current library in CLASSPATH
		nsExec::ExecToStack `java -classpath . FindInPath CLASSPATH $lib` 
		Pop $0 ; Pop return code from program from stack
		Pop $1 ; Pop stdout from program from stack (unused, but clears the stack)
		;Delete FindInPath.class ; Done with this program, delete it
		${If} $0 == "1"
			DetailPrint "Found library: $lib" ; Print that found library
			IntOp $libsFound $libsFound + 1 ; Increment number of found libraries
			${libsRedun->Push} $lib ; Add found library to libraries erase after copying
		${ElseIf} $0 == "0"
			DetailPrint "Did not find library: $lib" ; Print that did not find library
		${EndIf}
		IntOp $indx $indx + 1
	${Loop}
	DetailPrint "Found $libsFound of 3 libraries."
	${libs->Delete} ; Delete first array, done with
FunctionEnd

# Remove installed libraries that were found in CLASSPATH before
# installation and therefore are unneeded
Function removeDuplicates
	DetailPrint "Removing any duplicate libraries."
	StrCpy $lib "x" ; Make lib variable not blank initally
	IntOp $indx $indx - 3 ; Reset loop index to 0
	${DoUntil} $lib == ""
		${libsRedun->Read} $lib $indx ; Get each duplicate library
		Delete $INSTDIR\lib\$lib ; Delete each duplicate library from $INSTDIR
		IntOp $indx $indx + 1 ; Increment index
	${Loop}
	${libsRedun->Delete} ; Delete second array, done with
FunctionEnd

# Installer sections
Section -Main SEC0000
	Call detectJVM
	Call detectTPLibs
	SetOverwrite on
SectionEnd

# Component selection
InstType /COMPONENTSONLYONCUSTOM
Section "Gaudi tool" GaudiTool
    SectionIn 1 RO
    File gaudi.exe
    File license.txt
SectionEnd

# Prompt user if they want newly installed libraries to be  
# available to other JVM-based programs
Section "Third-party libraries" TPLibs
	${If} ${Cmd} `MessageBox MB_YESNO|MB_ICONQUESTION "Install libraries for all JVM applications?" IDYES`
		; ...
	${Else}
    	SetOutPath $INSTDIR\lib
	${EndIf}
    File lib\scala-library.jar
	File lib\json_simple-1.1.jar
	File lib\commons-io-1.4.jar
	Call removeDuplicates
SectionEnd

LangString DESC_GaudiTool ${LANG_ENGLISH} "Gaudi executable (required)."
LangString DESC_TPLibs ${LANG_ENGLISH} "Third party libraries Gaudi depends on."
!insertmacro MUI_FUNCTION_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${GaudiTool} $(DESC_GaudiTool)
  !insertmacro MUI_DESCRIPTION_TEXT ${TPLibs} $(DESC_TPLibs)
!insertmacro MUI_FUNCTION_DESCRIPTION_END

Section -post SEC0001
    WriteRegStr HKLM "${REGKEY}\Components" Main 1
    WriteRegStr HKLM "${REGKEY}" Path $INSTDIR
    SetOutPath $INSTDIR
    WriteUninstaller $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
    SetOutPath $SMPROGRAMS\$StartMenuGroup
    CreateShortcut "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk" $INSTDIR\uninstall.exe
    !insertmacro MUI_STARTMENU_WRITE_END
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayName "$(^Name)"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayVersion "${VERSION}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" Publisher "${COMPANY}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" URLInfoAbout "${URL}"
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" DisplayIcon $INSTDIR\uninstall.exe
    WriteRegStr HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" UninstallString $INSTDIR\uninstall.exe
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoModify 1
    WriteRegDWORD HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)" NoRepair 1 
    ${EnvVarUpdate} $0 "PATH" "A" "HKLM" $INSTDIR ; Append to PATH
SectionEnd

# Macro for selecting uninstaller sections
!macro SELECT_UNSECTION SECTION_NAME UNSECTION_ID
    Push $R0
    ReadRegStr $R0 HKLM "${REGKEY}\Components" "${SECTION_NAME}"
    StrCmp $R0 1 0 next${UNSECTION_ID}
    !insertmacro SelectSection "${UNSECTION_ID}"
    GoTo done${UNSECTION_ID}
next${UNSECTION_ID}:
    !insertmacro UnselectSection "${UNSECTION_ID}"
done${UNSECTION_ID}:
    Pop $R0
!macroend

# Uninstaller sections
Section /o -un.Main UNSEC0000
    Delete $INSTDIR\gaudi.exe
    Delete $INSTDIR\license.txt
	Delete $INSTDIR\*.log
    Delete $INSTDIR\lib\*
    DeleteRegValue HKLM "${REGKEY}\Components" Main
SectionEnd

Section -un.post UNSEC0001
    DeleteRegKey HKLM "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\$(^Name)"
    Delete "$SMPROGRAMS\$StartMenuGroup\Uninstall $(^Name).lnk"
    Delete $INSTDIR\uninstall.exe
    DeleteRegValue HKLM "${REGKEY}" StartMenuGroup
    DeleteRegValue HKLM "${REGKEY}" Path
    DeleteRegKey /IfEmpty HKLM "${REGKEY}\Components"
    DeleteRegKey /IfEmpty HKLM "${REGKEY}"
    RmDir $SMPROGRAMS\$StartMenuGroup
	RmDir $INSTDIR\lib
    RmDir $INSTDIR
    ${un.EnvVarUpdate} $0 "PATH" "R" "HKLM" $INSTDIR ; Remove from PATH
	${un.EnvVarUpdate} $0 "CLASSPATH" "R" "HKLM" $INSTDIR\libs ; Remove any libs installed with Gaudi from CP
SectionEnd


# Installer functions
Function .onInit
    InitPluginsDir
FunctionEnd

# Uninstaller functions
Function un.onInit
    ReadRegStr $INSTDIR HKLM "${REGKEY}" Path
    !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuGroup
    !insertmacro SELECT_UNSECTION Main ${UNSEC0000}
FunctionEnd

