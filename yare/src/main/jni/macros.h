#ifndef YARE_MACROS_H
#define YARE_MACROS_H

#define LIKELY(x) __builtin_expect(!!(x), 1)
#define UNLIKELY(x) __builtin_expect(!!(x), 0)

#endif
