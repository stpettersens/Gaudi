# Gaudi installation script (NSIS) for Windows
# Auto-generated in part by EclipseNSIS Script Wizard
#
# This script uses EnvVarUpdate.nsh from:
# http://nsis.sourceforge.net/Environmental_Variables:_append,_prepend,_and_remove_entries
#

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

# Variables
Var StartMenuGroup
Var FoundLibs

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

# Detect presence of a suitable JVM environment on system
# That is, that it exists and is at least version 1.5+ capable
Function detectJVM
	SetOutPath .
	File JavaCheck.class ; Extract small Java version checker program
	nsExec::ExecToStack `java -classpath . JavaCheck 1.5` ; Attempt to execute Java version checker program
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
	Delete JavaCheck.class ; Done with this program, delete it
	DetailPrint "Detected JVM: version $1" ; Display detected version 
	# Check this JVM meets minimum version requirement (v1.5.x)
	${If} $0 == "1"
		DetailPrint "JVM reports suitable version (1.5+)"
		GoTo goodJVM
	${Else}
		DetailPrint "JVM reports unsuitable version (< 1.5)"
		DetailPrint "Please update it."
		GoTo downloadJVM
		badJVM:
		DetailPrint "JVM requirement was not met, so installation was aborted."
		DetailPrint "Please download and/or install a suitable JVM and run this setup again."
		Abort
		goodJVM: 
	${EndIf}
FunctionEnd

# Detect if third-party libraries Gaudi requires are
# present on system. If so, add them to found JVM's CLASSPATH
Function detectTPLibs
	# TODO ...
	StrCpy $FoundLibs "dummyDir"
	;${EnvVarUpdate} $0 "CLASSPATH" "A" "HKLM" $FoundLibs ;TODO
FunctionEnd

# Prompt user if they want installed libraries to be available
# to other JVM-based programs, when called from detectTBLibs this is done automatically
Function addLibsToClassPath
	${If} ${Cmd} `MessageBox MB_YESNO|MB_ICONQUESTION "Add libraries to CLASSPATH?" IDYES`
		;${EnvVarUpdate} $0 "CLASSPATH" "A" "HKLM" $INSTDIR\libs ; Add libraries to CLASSPATH
	${EndIf}
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
    SetOutPath $INSTDIR
    File gaudi.exe
    File license.txt
SectionEnd

Section "Third-party libraries" TPLibs
    SetOutPath $INSTDIR\lib
    File lib\scala-library.jar
    File lib\json_simple-1.1.jar
    File lib\commons-io-1.4.jar
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
	;${un.EnvVarUpdate} $0 "CLASSPATH" "R" "HKLM" $FoundLibs ; Remove any found libs from CLASSPATH (CP)
	;${un.EnvVarUpdate} $0 "CLASSPATH" "R" "HKLM" $INSTDIR\libs ; Remove any libs installed with Gaudi from CP
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

