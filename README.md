# JoonerDb
一个用Java语言实现的K-V数据库
详细见文档 https://github.com/weixublog/joonerdb/blob/master/src/doc/JoonerDB%20%E6%96%87%E6%A1%A3.doc
有关命令如下
### 集合（SET） 指令
（1）SINTER key1 key2
```
SADD key1 1 2 8 9 12
SADD key2 1 3 5 7 9
SADD key3 2 4 6 8 22

SINTER key1 key2
SINTER key1 key3
SINTER key2 key3

SUNION key1 key2
SUNION key1 key3
SUNION key2 key3

SDIFFER key1 key2
SDIFFER key1 key3
SDIFFER key2 key3
```
（2）SREMOVE KEY VALUE
```
SADD set AAA BBB ccc HHH 3 4
SREMOVE set AAA
SREMOVE set aaa
SREMOVE set BBB
SREMOVE set ccC
SREMOVE set ccc
SREMOVE set HHH
SREMOVE set 3
SREMOVE set 4
```
（3）SLEN KEY
```
SADD set AAA BBB ccc HHH 3 4
SLEN set
SLEN set1
SADD set1
SLEN set1
```
### 映射（MAP） 指令
（1） MPUT KEY FIELD VALUE [FIELD VALUE]
```
MPUT userInfo userName Tom userAge 12
```
（2）MGET KEY [FIELD]
```JOONERDB
MGET userInfo 
MGET userInfo userName
MPUT userInfo userSex M
MGET userInfo userSex
```
（3）MREMOVE FIELD [FIELD]
```
MPUT userInfo userName Tom userAge 12
MPUT userInfo userSex F
MGET userInfo
MREMOVE userInfo userSex userName
MGET userInfo userSex
```
（4）MFIELDS KEY
```jooner
MPUT userInfo userName Tom userAge 12
MFIELDS userInfo
MPUT userInfo userSex F
MREMOVE userInfo userName
MFIELDS userInfo
```
（8）MLEN KEY
```jooner
MPUT userInfo userName Tom userAge 12
MLEN userInfo
MREMOVE userInfo userName
MLEN userInfo
MPUT userInfo userSex M
MLEN userInfo
```

### JSON 指令
```jooner

JSET testJson {"son":"lisi","userName":"zhangsan","userAge":"22"}
JSET testJson son {"sonName":"Tom","sonAge":"8"}
JSET testJson son subson {"hobby":"reading","tv":"哆啦A梦"}
JGET testJson
JGET testJson son
JSET testJson son {"sonName":"Tom","sonAge":"8"}
JGET testJson son
JSET testJson far {"farName":"Bob","farAge":"38"}
JPUT testJson son wangwu
JSET testJson son subson {"hobby":"reading","tv":"哆啦A梦"}
JSET testJson son {"subson":{"hobby":"reading","tv":"哆啦A梦"}}

JREMOVE testJson userAge
```