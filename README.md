# seeker
一个封装了MongoDB插入和查询和自然语言查询MongoDB数据的后台。

## 环境搭建
克隆整个项目
```sh
git clone git@github.com:amazinglzy/seeker.git
```

下载Stanford Parser的中文Model到`lib`文件夹下(如果没有该文件夹，则在项目根目录下新建)。
```sh
wget https://nlp.stanford.edu/software/stanford-chinese-corenlp-2018-02-27-models.jar
```

同时，下载Stanford Parser CoreNLP 3.9.1，将其中的Model(stanford-corenlp-3.9.1-models.jar)复制到`lib`文件夹下。
```sh
wget https://nlp.stanford.edu/software/stanford-corenlp-full-2018-02-27.zip
```

修改`edu.ustb.seeker.controller.MongoController.java`文件，配置MongoDB数据库。
```java
this.mc = new MongoContact("mongodb://seeker:seeker@192.168.56.129:27017/?authSource=seeker");
```

## 一些说明
1. 该系统中MongoDB的数据，键不是简单的英文简写，`{'id': 1, 'key': '342', 's': 234}`这种，而是`{"河流的名称": "黄河"}`这种，因为系统会处理键的信息来完成自然语言查询语句到MongoDB的查询语句的翻译过程。
2. 该系统实现自然语言查询数据的主要思路是，将自然语言语句处理成依存树后，提取出主语，设计算法将提取出的词语对应到MongoDB文档中具体的键上，再将剩下的词语按照一定的规则提取信息构造成剩下的查询JSON文档。
3. `settings`文件夹中，`hownet_dict`存放的是hownet的词库，`valueGenerateRules`是一些正则表达式，表示从自然语言中提取数据的规则，具体正则表达式的定义在后面介绍，`import_phrase.properties`是查询语句中的一些关键的词的标记，`stop_phrase.txt`里面存放的是停用词，`syno.txt`是一个停用词库。
4. 正则表达式规则如下：

    + "s" s为一个字符串，是自然语言中的一个词。
    + {s} s为一个字符串，s是该字符集S={Lt, Gt, Equ, Range, And, Not, Or, Not}中的一个，每个元素均有一系列的词对应在其中，该内容存储在知识库中。
    + [s] s为一个字符串，s是自然语言处理中领域命名实体标注的结果，比如100和1万就会被标注成NUMBER。
    + ? 表示可以匹配任意的自然语言中的词。但是不包括命名实体标记中非O的词和带系统关注的词义的词以及标点符号(主要目的是为了消除歧义)。
    + \* 表示匹配前面的表达式零次或多次。
    + (S) S可以为一个表达式，也可以为多个表达式，()可以将S当作一个表达式处理，其主要是为 * 服务。例如 ([NUMBER]"，")* 就可以匹配 “3，5，6，”这样的字符串。()可以嵌套使用，但是其不能交叉，因为会产生歧义。
    + \<S>var/TYPE 跟()一样，S可以为一个表达式，也可以为多个表达式，var是一个变量名，而TYPE属于集合T={STRING, NUMBER}，该表达式的作用是新建一个变量名为var，类型是TYPE是一个变量，同时将S表示的自然语言的词或词序列转化成对应的类型存储到var变量中。
1. 至于对应到键的算法，其输入是两个词语序列(第一个是提取出来的，另外一个是MongoDB的键分词加上停用词处理之后的结果)，输出是一个数表示其相关度。实现方法是建立一个流图，节点包括一个源点S，一个汇点T，两个词语序列的每个词建立一个节点，S到每一个第一个词语序列的点，建立一条费用为0，流量为1的边，对于每一个第二个词语序列的点，建立一条费用为0，流量为1的到T的边。对于任意个二元组$(w_1, w_2)$，$w_1$在第一个词语序列，$w_2$在第二个词语序列中，建立一条费用为$w_1$与$w_2$的相似度，流量为1的边，跑最大费用流的费用作为词语序列的相似度。至于词语与词语之间的相似度，是用基于hownet来实现的(参考论文“基于《知网》的词汇语义相似度计算”)。