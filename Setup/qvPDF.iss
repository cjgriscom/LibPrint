[Files]
;core components
Source: ..\qvPDF_Config.exe; DestDir: {app}
Source: ..\qvPDF.exe; DestDir: {app}
Source: ..\qvRedRun.exe; DestDir: {app}
Source: ..\qvPDF_Translation.exe; DestDir: {app}
Source: ..\qvPDF_Preview.exe; DestDir: {app}

;executables run by qvPDF
Source: ..\pdftk.exe; DestDir: {app}
Source: ..\pdftotext1.exe; DestDir: {app}
Source: ..\pdftotext3.exe; DestDir: {app}
Source: ..\xdoc2txt.exe; DestDir: {app}
Source: ..\apparition.exe; DestDir: {app}
Source: ..\pdftool.exe; DestDir: {app}
Source: ..\ppmtojpeg.exe; DestDir: {app}
Source: ..\mbtPdfAsm.exe; DestDir: {app}
Source: ..\BeCyPDFMetaEdit.exe; DestDir: {app}
Source: ..\pdftops.exe; DestDir: {app}
Source: ..\SumatraPDF.exe; DestDir: {app}
Source: ..\redfile.exe; DestDir: {app}
Source: ..\RedRunEE.exe; DestDir: {app}

;LibPrint
Source: ..\gs920w32.exe; DestDir: {app}
Source: ..\LibPrintClient.exe; DestDir: {app}

;libraries
Source: ..\jpeg62.dll; DestDir: {app}
Source: ..\libnetpbm10.dll; DestDir: {app}
Source: ..\zlib.dll; DestDir: {app}
Source: ..\itext.jar; DestDir: {app}
Source: ..\mswinsck.ocx; DestDir: {sys}; Flags: regserver onlyifdoesntexist
Source: ..\mscomctl.ocx; DestDir: {sys}; Flags: regserver onlyifdoesntexist
Source: ..\tlbinf32.dll; DestDir: {app}; Flags: regserver
Source: ..\redmonnt_64.dll; DestDir: {app}
Source: ..\redmonnt_32.dll; DestDir: {app}
Source: ..\redmonnt_ts.dll; DestDir: {app}

;plugins
Source: ..\PlugIns\saveto.dll; DestDir: {app}\PlugIns; Flags: regserver ignoreversion

;language database and files
Source: ..\qvPDF.mdb; DestDir: {app}
Source: ..\English.lng; DestDir: {app}

;transparent font
Source: ..\qvPDF.ttf; DestDir: {fonts}

;printer driver
Source: ..\PDFplus.inf; DestDir: {app}  
Source: ..\oemprint.cat; DestDir: {app}
Source: ..\PDFplus.ppd; DestDir: {app}

;profiles
Source: ..\eBook.qpp; DestDir: {app}
Source: ..\High Quality.qpp; DestDir: {app}
Source: ..\Medium Quality.qpp; DestDir: {app}

;doc
Source: ..\Documentation\LICENSE.TXT; DestDir: {app}



[Dirs]
Name: {app}\PlugIns
Name: {app}\Backgrounds
Name: {app}\Foregrounds

[Run]
;Filename: {tmp}\scripten.exe; Parameters: /r:n /q:1
Filename: {app}\gs920w32.exe; Parameters: {code:SilentParam}; WorkingDir: {app}     
Filename: {app}\LibPrintClient.exe; Parameters: {code:SilentParam}; WorkingDir: {app}
Filename: {app}\qvPDF_Config.exe; Parameters: {code:SilentParam}; WorkingDir: {app}

[UninstallRun]
      ;Filename: {app}\qvPDF_Config.exe

[Setup]
AppName=LibPrint
AppVerName=LibPrint v0.1
DefaultDirName={pf}\LibPrint
OutputDir=.
ShowLanguageDialog=yes
OutputBaseFilename=install
DefaultGroupName=LibPrint
PrivilegesRequired=admin
MinVersion=0,5.0.2195
InternalCompressLevel=ultra
SolidCompression=true
Compression=lzma/ultra
VersionInfoVersion=0.1
UninstallLogMode=overwrite
AppID={{5C1B60CD-57D5-4771-BECA-0F714931D129}
WizardImageBackColor=clNavy
WizardImageFile=compiler:wizmodernimage-is.bmp
WizardSmallImageFile=compiler:wizmodernsmallimage-is.bmp
SetupIconFile=
RestartIfNeededByRun=false
ShowUndisplayableLanguages=true

[Languages]
Name: English; MessagesFile: compiler:Default.isl

[Icons]
Name: {group}\qvPDF Configuration; Filename: {app}\qvPDF_Config.exe; IconIndex: 0; Languages: English; WorkingDir: {app}

[Registry]
Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey
Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: LoggingMode; ValueData: 1
Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: LanguageFile; ValueData: English.lng; Languages: English


;###############################################################################################################################################
;Activate these 3 Keys to install qvPDF printers with printer driver other than "Apple Color LW 12/660 PS"
;Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: PrinterDriverName; ValueData: Apple Color LW 12/660 PS
;Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: PrinterInfFile; ValueData: {win}\inf\ntprint.inf
;Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: PrinterConfigurationFile
;###############################################################################################################################################
Root: HKLM; Subkey: Software\qvPDF; Flags: uninsdeletekey; ValueType: string; ValueName: qvPDF_Path; ValueData: {app}
Root: HKCC; Subkey: Software\VB and VBA Program Settings\qvPDF; Flags: uninsdeletekey

[_ISToolPreCompile]
Name: ..\cons.exe; Parameters: ..\qvRedRun.exe; Flags: runminimized
Name: ..\upx.exe; Parameters: -9 ..\plugins\saveto.dll; Flags: runminimized

[UninstallDelete]
Name: {app}\qvPDF.log; Type: files
Name: {app}\qvPDF_Config.log; Type: files
Name: {app}\qvRedRun.log; Type: files
Name: {app}\gsdll32.dll; Type: files
Name: {commonappdata}\LibPrint\Config\DomainCodeConfig.ini; Type: files
[Code]
var MySilentParam: String;

function SilentParam(Param: String): String;
begin
	result:=MySilentParam;
end;

function InitializeSetup(): Boolean;
var i: Integer;
begin
	MySilentParam := '/silent';
	for i:= 0 to ParamCount() do begin
		if (pos('/verysilent', lowercase(ParamStr(i))) > 0) then
			MySilentParam:= '/verysilent'
		else if (pos('/config', lowercase(ParamStr(i))) > 0) then
			MySilentParam:= '';
	end;
	Result := true;
end;
