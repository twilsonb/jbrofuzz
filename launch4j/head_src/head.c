/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (C) 2004, 2006 Grzegorz Kowal

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA


	Compiled with Mingw port of GCC,
	Bloodshed Dev-C++ IDE (http://www.bloodshed.net/devcpp.html)
*/

#include "resource.h"
#include "head.h"

BOOL debug = FALSE;
BOOL console = FALSE;
int foundJava = NO_JAVA_FOUND;

struct _stat statBuf;
PROCESS_INFORMATION pi;

char errTitle[STR] = "Launch4j";
char javaMinVer[STR] = {0};
char javaMaxVer[STR] = {0};
char foundJavaVer[STR] = {0};

char workingDir[_MAX_PATH] = {0};
char cmd[_MAX_PATH] = {0};
char args[MAX_ARGS] = {0};

void setConsoleFlag() {
     console = TRUE;
}

void titledMsgBox(const char* title, const char* text) {
    if (console) {
        printf("%s: %s\n", title, text);
    } else {
    	MessageBox(NULL, text, title, MB_OK);
    }
}

void msgBox(const char* text) {
    titledMsgBox(errTitle, text);
}

void showJavaWebPage() {
	ShellExecute(NULL, "open", "http://java.com/download", NULL, NULL, SW_SHOWNORMAL);
}

BOOL loadString(HMODULE hLibrary, int resID, char* buffer) {
	HRSRC hResource;
	HGLOBAL hResourceLoaded;
	LPBYTE lpBuffer;

	hResource = FindResourceEx(hLibrary, RT_RCDATA, MAKEINTRESOURCE(resID),
			MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT));
	if (NULL != hResource) {
		hResourceLoaded = LoadResource(hLibrary, hResource);
		if (NULL != hResourceLoaded) {
			lpBuffer = (LPBYTE) LockResource(hResourceLoaded);            
			if (NULL != lpBuffer) {     
				int x = 0;
				do {
					buffer[x] = (char) lpBuffer[x];
				} while (buffer[x++] != 0);
				return TRUE;
			}
		}    
	}
	return FALSE;
}

BOOL loadBoolString(HMODULE hLibrary, int resID) {
	char boolStr[10] = {0};
	loadString(hLibrary, resID, boolStr);
	return strcmp(boolStr, TRUE_STR) == 0;
}

void regSearch(HKEY hKey, const char* keyName, int searchType) {
	DWORD x = 0;
	unsigned long size = BIG_STR;
	FILETIME time;
	char buffer[BIG_STR] = {0};
	while (RegEnumKeyEx(
				hKey,			// handle to key to enumerate
				x++,			// index of subkey to enumerate
				buffer,			// address of buffer for subkey name
				&size,			// address for size of subkey buffer
				NULL,			// reserved
				NULL,			// address of buffer for class string
				NULL,			// address for size of class buffer
				&time) == ERROR_SUCCESS) {
		if (strcmp(buffer, javaMinVer) >= 0
				&& (javaMaxVer[0] == 0 || strcmp(buffer, javaMaxVer) <= 0)
				&& strcmp(buffer, foundJavaVer) > 0) {
			strcpy(foundJavaVer, buffer);
			foundJava = searchType;
		}
		size = BIG_STR;
	}
}

BOOL findJavaHome(char* path) {
	HKEY hKey;
	const char jre[] = "SOFTWARE\\JavaSoft\\Java Runtime Environment";
	const char sdk[] = "SOFTWARE\\JavaSoft\\Java Development Kit";
	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE,
			TEXT(jre),
			0,
            KEY_QUERY_VALUE | KEY_ENUMERATE_SUB_KEYS,
			&hKey) == ERROR_SUCCESS) {
		regSearch(hKey, jre, FOUND_JRE);
		RegCloseKey(hKey);
	}
	if (RegOpenKeyEx(HKEY_LOCAL_MACHINE,
			TEXT(sdk),
			0,
            KEY_QUERY_VALUE | KEY_ENUMERATE_SUB_KEYS,
			&hKey) == ERROR_SUCCESS) {
		regSearch(hKey, sdk, FOUND_SDK);
		RegCloseKey(hKey);
	}
	if (foundJava != NO_JAVA_FOUND) {
		char keyBuffer[BIG_STR];
		unsigned long datatype;
		unsigned long bufferlength = BIG_STR;
		if (foundJava == FOUND_JRE)	{
			strcpy(keyBuffer, jre);
		} else {
			strcpy(keyBuffer, sdk);
		}
		strcat(keyBuffer, "\\");
		strcat(keyBuffer, foundJavaVer);
		if (RegOpenKeyEx(HKEY_LOCAL_MACHINE,
			TEXT(keyBuffer),
			0,
            KEY_QUERY_VALUE,
			&hKey) == ERROR_SUCCESS) {
			unsigned char buffer[BIG_STR] = {0};
			if (RegQueryValueEx(hKey, "JavaHome", NULL, &datatype, buffer, &bufferlength)
					== ERROR_SUCCESS) {
				int i = 0;
				do {
					path[i] = buffer[i];
				} while (path[i++] != 0);
				if (foundJava == FOUND_SDK) {
					strcat(path, "\\jre");
				}
				RegCloseKey(hKey);
				return TRUE;
			}
			RegCloseKey(hKey);
		}
	}
	return FALSE;
}

/*
 * Extract the executable name, returns path length.
 */
int getExePath(char* exePath) {
	HMODULE hModule = GetModuleHandle(NULL);
    if (hModule == 0
			|| GetModuleFileName(hModule, exePath, _MAX_PATH) == 0) {
        return -1;
    }
	return strrchr(exePath, '\\') - exePath;
}

void appendJavaw(char* jrePath) {
    if (console) {
	    strcat(jrePath, "\\bin\\java.exe");
    } else {
        strcat(jrePath, "\\bin\\javaw.exe");
    }
}

void appendLauncher(BOOL setProcName, char* exePath, int pathLen, char* cmd) {
	if (setProcName) {
		char tmpspec[_MAX_PATH];
		char tmpfile[_MAX_PATH];
		strcpy(tmpspec, cmd);
		strcat(tmpspec, LAUNCH4J_TMP_DIR);
		tmpspec[strlen(tmpspec) - 1] = 0;
		if (_stat(tmpspec, &statBuf) == 0) {
			// remove temp launchers
			struct _finddata_t c_file;
			long hFile;
			strcat(tmpspec, "\\*.exe");
			strcpy(tmpfile, cmd);
			strcat(tmpfile, LAUNCH4J_TMP_DIR);
			char* filename = tmpfile + strlen(tmpfile);
			if ((hFile = _findfirst(tmpspec, &c_file)) != -1L) {
				do {
					strcpy(filename, c_file.name);
					_unlink(tmpfile);
				} while (_findnext(hFile, &c_file) == 0);
			}
			_findclose(hFile);
		} else {
			if (_mkdir(tmpspec) != 0) {
				appendJavaw(cmd);
				return;
			}
		}
		char javaw[_MAX_PATH];
		strcpy(javaw, cmd);
		appendJavaw(javaw);
		strcpy(tmpfile, cmd);
		strcat(tmpfile, LAUNCH4J_TMP_DIR);
		char* exeFilePart = exePath + pathLen + 1;
		strcat(tmpfile, exeFilePart);
		if (CopyFile(javaw, tmpfile, FALSE)) {
			strcpy(cmd, tmpfile);
			return;
		} else if (_stat(javaw, &statBuf) == 0) {
			long fs = statBuf.st_size;
			if (_stat(tmpfile, &statBuf) == 0 && fs == statBuf.st_size) {
				strcpy(cmd, tmpfile);
				return;
			}
		}
	}
	appendJavaw(cmd);
}

BOOL isJrePathOk(char* path) {
	if (!*path) {
		return FALSE;
	}
	char javaw[_MAX_PATH];
	strcpy(javaw, path);
	appendJavaw(javaw);
	return _stat(javaw, &statBuf) == 0;
}

BOOL prepare(HMODULE hLibrary, char *lpCmdLine) {
    char tmp[MAX_ARGS] = {0};
    GetEnvironmentVariable("launch4j", tmp, STR);
    debug = _stricmp(tmp, "debug") == 0;

	// Open executable
	char exePath[_MAX_PATH] = {0};
	int pathLen = getExePath(exePath);
	if (pathLen == -1) {
		msgBox("Cannot determinate exe file name.");
		return FALSE;
	}
	hLibrary = LoadLibrary(exePath);
	if (hLibrary == NULL) {
		char msg[BIG_STR];
        strcpy(msg, "Cannot find file: ");
		strcat(msg, exePath);
		msgBox(msg);
		return FALSE;
	}

	// Error message box title
	loadString(hLibrary, ERR_TITLE, errTitle);

	// Working dir
	char tmp_path[_MAX_PATH] = {0};
	if (loadString(hLibrary, CHDIR, tmp_path)) {
		strncpy(workingDir, exePath, pathLen);
		strcat(workingDir, "\\");
		strcat(workingDir, tmp_path);
		_chdir(workingDir);
	}

	// Custom process name
	const BOOL setProcName = loadBoolString(hLibrary, SET_PROC_NAME);

	// Use bundled jre or find java
	if (loadString(hLibrary, JRE_PATH, tmp_path)) {
		if (tmp_path[0] == '\\' || tmp_path[1] == ':') {
			// Absolute
			strcpy(cmd, tmp_path);
		} else {
			// Relative
			strncpy(cmd, exePath, pathLen);
			strcat(cmd, "\\");
			strcat(cmd, tmp_path);
		}
    }
	if (!isJrePathOk(cmd)) {
		if (!loadString(hLibrary, JAVA_MIN_VER, javaMinVer)) {
			msgBox("Cannot find bundled JRE or javaw.exe is missing.");
			return FALSE;
		}
		loadString(hLibrary, JAVA_MAX_VER, javaMaxVer);
		if (!findJavaHome(cmd)) {
			char txt[BIG_STR];
			strcpy(txt, "Cannot find Java ");
			strcat(txt, javaMinVer);
			if (*javaMaxVer) {
				strcat(txt, " - ");
				strcat(txt, javaMaxVer);
			}
			msgBox(txt);
			showJavaWebPage();
			return FALSE;
		}
		if (!isJrePathOk(cmd)) {
			msgBox("Java found, but javaw.exe seems to be missing.");
			return FALSE;
		}
	}

    // Append a path to the Path environment variable
	char jreBinPath[_MAX_PATH];
	strcpy(jreBinPath, cmd);
	strcat(jreBinPath, "\\bin");
	if (!appendToPathVar(jreBinPath)) {
		msgBox("Cannot set the Path environment variable.");
		return FALSE;
	}

	appendLauncher(setProcName, exePath, pathLen, cmd);

    // JVM args
	if (loadString(hLibrary, JVM_ARGS, tmp)) {
		strcat(tmp, " ");
	} else {
        *tmp = 0;
    }
	// Load additional JVM arguments from ini file
	strncpy(tmp_path, exePath, strlen(exePath) - 3);
	strcat(tmp_path, "ini");
	long hFile;
	if ((hFile = _open(tmp_path, _O_RDONLY)) != -1) {
		const int jvmArgsLen = strlen(tmp);
		char* src = tmp + jvmArgsLen;
		char* dst = src;
		const int len = _read(hFile, src, MAX_ARGS - jvmArgsLen - BIG_STR);
		int i;
		for (i = 0; i < len; i++) {
			if (*src == 13 || *src == 10) {
				if (dst > tmp && *(dst - 1) != ' ') {
					*dst++ = ' ';
				}
				src++;
			} else {
				*dst++ = *src++;
			}
		}
		*dst = 0;
		if (len > 0 && *(dst - 1) != ' ') {
			strcat(tmp, " ");
		}
		_close(hFile);
	}

    // Expand environment %variables%
    if (*tmp) {
        char varName[STR];
        char varValue[MAX_VAR_SIZE];
        char *pos = tmp;
        while (strlen(pos) > 0) {
            char *start = strchr(pos, '%');
            if (start != NULL) {
                char *end = strchr(start + 1, '%');
                if (end == NULL) {
                    return FALSE;
                }
                // Copy content up to %VAR%
                strncat(args, pos, start - pos);
                // Insert value of %VAR%
                varName[0] = 0;
                strncat(varName, start + 1, end - start - 1);
                if (strcmp(varName, "EXEDIR") == 0) {
                    strncat(args, exePath, pathLen);
                } else if (strcmp(varName, "EXEFILE") == 0) {
                    strcat(args, exePath);                    
                } else if (GetEnvironmentVariable(varName, varValue, MAX_VAR_SIZE) > 0) {
                    strcat(args, varValue);
                }
                pos = end + 1;
            } else {
                // Copy remaining content
                strcat(args, pos);
                break;
            }
        }
	}

	// Jar
	strcat(args, "-jar \"");
	if (loadString(hLibrary, JAR, tmp)) {
        strncat(args, exePath, pathLen);
        strcat(args, "\\");
        strcat(args, tmp);
    } else {
    	strcat(args, exePath);
    }
	strcat(args, "\"");

	// Constant command line args
	char jarArgs[BIG_STR] = {0};
	if (loadString(hLibrary, JAR_ARGS, jarArgs)) {
		strcat(args, " ");
		strcat(args, jarArgs);
	}

	// Command line args
	if (*lpCmdLine) {
		strcat(args, " ");
		strcat(args, lpCmdLine);
	}

    if (debug) {
        printf("*** Launch4j debug info ***\nWorking dir:\t%s\n", workingDir);
		printf("Launcher:\t%s\n", cmd);
        _itoa(strlen(args), tmp, 10);     // 10 -- radix
		printf("Args length:\t%s/32768 chars\n", tmp);
		printf("Launcher args:\t%s\n\n", args);
    }
	return TRUE;
}

void closeHandles() {
	CloseHandle(pi.hThread);
	CloseHandle(pi.hProcess);
}

/*
 * Append a path to the Path environment variable
 */
BOOL appendToPathVar(char* path) {
	char chBuf[MAX_VAR_SIZE] = {0};

	const int pathSize = GetEnvironmentVariable("Path", chBuf, MAX_VAR_SIZE);
	if (MAX_VAR_SIZE - pathSize - 1 < strlen(path)) {
		return FALSE;
	}
	strcat(chBuf, ";");
	strcat(chBuf, path);
	return SetEnvironmentVariable("Path", chBuf);
}

DWORD execute(BOOL wait) {
	STARTUPINFO si;
    memset(&pi, 0, sizeof(pi));
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);

	DWORD dwExitCode = -1;
	char cmdline[MAX_ARGS];
    strcpy(cmdline, "\"");
	strcat(cmdline, cmd);
	strcat(cmdline, "\" ");
	strcat(cmdline, args);
	if (CreateProcess(NULL, cmdline, NULL, NULL,
			TRUE, 0, NULL, NULL, &si, &pi)) {
		if (wait) {
			WaitForSingleObject(pi.hProcess, INFINITE);
			GetExitCodeProcess(pi.hProcess, &dwExitCode);
			closeHandles();
		} else {
			dwExitCode = 0;
		}
	}
	return dwExitCode;
}
