#ifndef __MTEERR_H__
#define __MTEERR_H__

#define MTE_OK              (0)
#define MTE_CONFIG          (-1)
#define MTE_SRVUNAVAIL      (-2)
#define MTE_LOGERROR        (-3)
#define MTE_INVALIDCONNECT  (-4)
#define MTE_NOTCONNECTED    (-5)
#define MTE_WRITE           (-6)
#define MTE_READ            (-7)
#define MTE_TSMR            (-8)
#define MTE_NOMEMORY        (-9)
#define MTE_ZLIB            (-10)
#define MTE_PKTINPROGRESS   (-11)
#define MTE_PKTNOTSTARTED   (-12)
#define MTE_FATALERROR      (-13) // new CMA error code, previously was MTE_LOGON for TEServer 
#define MTE_INVALIDHANDLE   (-14)
#define MTE_DSROFF          (-15)
#define MTE_ERRUNKNOWN      (-16)
#define MTE_BADPTR          (-17)
#define MTE_WRONGPARAM      (-17) // CMA synonim for MTE_BADPTR
#define MTE_TRANSREJECTED   (-18)
#define MTE_REJECTION       (-18) // CMA synonim for MTE_TRANSREJECTED
// CMA error codes
#define MTE_TEUNAVAIL       (-19) // new CMA error code, previously was MTE_TOOSLOWCONNECT for TEServer
#define MTE_NOTLOGGEDIN     (-20) // new CMA error code, previously was MTE_CRYPTO_ERROR for TEServer
#define MTE_WRONGVERSION    (-21) // new CMA error code, previously was MTE_THREAD_ERROR for TEServer
// MICEX Bridge errors again
#define MTE_LOGON           (-30) // previously was -13, changed for CMA compatibility
#define MTE_TOOSLOWCONNECT  (-31) // previously was -19, changed for CMA compatibility
#define MTE_CRYPTO_ERROR    (-32) // previously was -20, changed for CMA compatibility
#define MTE_THREAD_ERROR    (-33) // previously was -21, changed for CMA compatibility
#define MTE_NOTIMPLEMENTED  (-34)
#define MTE_ABANDONED       (-35) // MTExxx() call was interrupted by asynchronous MTEDisconnect()

#endif
