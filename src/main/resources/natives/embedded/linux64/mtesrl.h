#ifndef __MTESRL_H__
#define __MTESRL_H__

#include <stdint.h>
#include <stdio.h>
#include "mteerr.h"


#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
	int32_t  DataLen;
	char     Data[1];
} MTEMSG;

typedef char ERRORBUF[256];

// Field attributes
// Key field
#define mffKey       0x0001
// Contains security code
#define mffSecCode   0x0002
// Cannot be null
#define mffNotNull   0x0004
// Member of repeatable fields group
#define mffVarBlock  0x0008
// Empty value can be encoded as sharp (#)
#define mffSharp     0x0080
// Fixed mask
#define ffFixedMask  0x0030
// ftFixed modifier: Decimals should be 1, not 2
#define ffFixed1     0x0010
// ftFixed modifier: Decimals should be 3, not 2
#define ffFixed3     0x0020
// ftFixed modifier: Decimals should be 4, not 2
#define ffFixed4     0x0030

// Table attributes
// Is updateable
#define mmfUpdateable      0x0001
// Should be cleared when updates received
#define mmfClearOnUpdate   0x0002
// Contains orderbook information
#define mmfOrderBook       0x0004


// Field data type
//   ftFloatPoint available only in MTEStructureEx(version>=3)
typedef enum {ftChar, ftInteger, ftFixed, ftFloat, ftDate, ftTime, ftFloatPoint} MteFieldType;

// Enumeration kinds
typedef enum {ekCheck=0, egGroup, ekCombo} MteEnumKind;

// Connection property flags
#define		ZLIB_COMPRESSED		0x1   // session is using ZLIB compression
#define		FLAG_ENCRYPTED		0x2   // session traffic is encrypted
#define		FLAG_SIGNING_ON		0x4   // session transactios are digitally signed
#define		FLAG_RECONNECTING	0x8   // reconnect in progress

typedef struct
{
        int32_t  Size;         // must be set to sizeof(TConnectionStats) prior to call
        uint32_t Properties;   // ZLIB_COMPRESSED, FLAG_ENCRYPTED, FLAG_SIGNING_ON
        uint32_t SentPackets;  // number of packets sent in the current session
        uint32_t RecvPackets;  // number of packets received in the current session
        uint32_t SentBytes;    // number of bytes sent in the current session
        uint32_t RecvBytes;    // number of bytes received in the current session
        // fields added in version 2
        uint32_t ServerIPAddress;
        uint32_t ReconnectCount;
        uint32_t SentUncompressed;
        uint32_t RecvUncompressed;
        char ServerName[64];
        // fields added in version 3
        uint32_t TsmrPacketSize;
        uint32_t TsmrSent;
        uint32_t TsmrRecv;
} ConnectionStats;


typedef struct TransParam {
        char* name;
        char* value;
} MteTransParam;

typedef struct TransReply {
        int32_t errCode;
        int32_t msgCode;
        char*   msgText;
        int32_t paramCount;
        MteTransParam* params;
} MteTransReply;

typedef struct TransResult {
        uint32_t replyCount;
        MteTransReply* replies;
} MteTransResult;

typedef struct SnapTable {
        int32_t handle;
        char*   name;
        char*   params;
} MteSnapTable;

#ifdef WIN32
#define __callspec __stdcall
#else
#define __callspec 
#endif

char* __callspec MTEErrorMsg(int32_t code);
char* __callspec MTEErrorMsgEx(int32_t code, const char *language);

int32_t __callspec MTEConnect(const char *params, char *error);
int32_t __callspec MTEDisconnect(int32_t conno);
int32_t __callspec MTEStructure(int32_t conno, MTEMSG **msg);
int32_t __callspec MTEExecTrans(int32_t conno, const char *name, const char *params, char *error);
int32_t __callspec MTEExecTransIP(int32_t conno, const char *name, const char *params, char *error, int32_t clientIP);

int32_t __callspec MTEOpenTable(int32_t conno, const char *name, const char *params, int32_t complete, MTEMSG **msg);
int32_t __callspec MTEAddTable(int32_t conno, int32_t tabno, int32_t ref);
int32_t __callspec MTERefresh(int32_t conno, MTEMSG **msg);
int32_t __callspec MTECloseTable(int32_t conno, int32_t tabno);
int32_t __callspec MTEFreeBuffer(int32_t conno);

int32_t __callspec MTEGetSnapshot(int32_t conno, char **buffer, int32_t *len);
int32_t __callspec MTESetSnapshot(int32_t conno, const char *buffer, int32_t len, char *error);

int32_t __callspec MTEGetExtData(int32_t conno, const char *DataSetName, const char *ExtFileName, MTEMSG **msg);
int32_t __callspec MTEGetExtDataRange(int32_t conno, const char *DataSetName, const char *ExtFileName,
                                     uint32_t DataOffset, uint32_t DataSize, MTEMSG **msg);
int32_t __callspec MTEGetServInfo(int32_t conno, char **buffer, int32_t *len);
int32_t __callspec MTEConnectionStats(int32_t conno, ConnectionStats *stats);
int32_t __callspec MTESelectBoards(int32_t conno, const char *boards, char *result);

int32_t __callspec MTEConnectionStatus(int32_t conno);
char* __callspec MTEGetVersion();

// Extended functions:
// mtesrl >= v.4.1.0.797
int32_t __callspec MTEStructure2(int32_t conno, MTEMSG **msg);
int32_t __callspec MTEExecTransEx(int32_t conno, const char *name, const char *params, int32_t clientIP, MteTransResult *result);
// mtesrl >= v.4.1.0.805
int32_t __callspec MTEGetTablesFromSnapshot(int32_t conno, const char* snapshot, int32_t len, MteSnapTable** tables);
int32_t __callspec MTEOpenTableAtSnapshot(int32_t conno, const char *name, const char *params, const char *snapshot, int32_t len, MTEMSG **msg);

// mtesrl >= v.4.2.0.1124
#define STRUCTURE_VERSION_MASK  0x00FF
#define STRUCTURE_OPTIONS_MASK  0xFF00
#define STRUCTURE_LOCALIZATION  0x0100
#define STRUCTURE_VERSION_0  0x0000
#define STRUCTURE_VERSION_1  0x0001
#define STRUCTURE_VERSION_2  0x0002
#define STRUCTURE_VERSION_3  0x0003
#define STRUCTURE_VERSION_4  0x0004
  
int32_t __callspec MTEStructureEx(int32_t conno, int32_t version, MTEMSG **msg);

// mtesrl >= v4.3.2.1144
int32_t __callspec MTEConnectEx(const char *params, MteTransResult *result);

#ifdef __cplusplus
}
#endif

#endif // __MTESRL_H__
