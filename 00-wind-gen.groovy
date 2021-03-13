package com.wind

import com.intellij.database.model.DasTable
import com.intellij.database.model.DasColumn
import com.intellij.database.model.ObjectKind
import com.intellij.database.util.Case
import com.intellij.database.util.DasUtil
import groovy.json.JsonSlurper

/**
 * 00-wind-gen-config.json 설명: (json에 설명 넣을수 없어 여기에 둠)
 *  {
 *        "bizName": "sample",  //업무이름
 *        "bizNameDynamicYn": false,    업무이름 동적 생성(table명에서 두번째 필드)
 *        "packageNameBase": "com.shinsegae.villiv",    //기본 패키지 명
 *        "removeTablePrefix": true,    //table명에서 첫번째 필드 제거
 *
 *        "relationship": {     //table 관계
 *            "showErrorWhenNotSelectSubTable": true, // 자식 table 같이 선택안해주면 에러
 *            "oneToMany": [    //1대다
 *               {"tableOne": "TLSS_SAMPLE", "tableMany": "TLSS_SAMPLE_ATTACH"}
 *            ]
 *        }
 *    }
 */


/**
 *
 * --------------------------------------
 * 1. datagrip에서 table로부터 소스코드 생성
 * 2. 부모자식 관계 관련 소스를 생성하려면 00-wind-gen-config.json에 세팅해주고
 *  부모,자식 table을 같이 선택해서 코드 생성해주어야 함
 * --------------------------------------
 *
 * Available context bindings:
 *   SELECTION   Iterable<DasObject>
 *   PROJECT     project
 *   FILES       files helper
 */


//=============== [s] inputs ===============
INPUT = [:]  //empty map

//= 사용자별 template 위치
//INPUT.TEMPLATE_BASE = "C:/Users/mr838/AppData/Roaming/JetBrains/DataGrip2020.3/extensions/com.intellij.database/schema/template"
INPUT.TEMPLATE_BASE = "/Users/wind/Library/Preferences/DataGrip2019.3/extensions/com.intellij.database/schema/template"
INPUT.JSON_CONFIG_PATH = INPUT.TEMPLATE_BASE + '/../00-wind-gen-config.json'

//== table 관계 설정
def jsonSlurper = new JsonSlurper()
def jsonConfig = jsonSlurper.parse(new File(INPUT.JSON_CONFIG_PATH))
INPUT.putAll(jsonConfig)

//== config assert
assert INPUT.containsKey("bizName")
assert INPUT.containsKey("bizNameDynamicYn")
assert INPUT.containsKey("packageNameBase")
assert INPUT.containsKey("removeTablePrefix")
assert INPUT.relationship != null
assert INPUT.relationship.oneToMany != null
assert INPUT.relationship.containsKey("showErrorWhenNotSelectSubTable")



//==
INPUT.ITEMS = [:]
INPUT.ITEMS.Info = [
    filePrefix : '',
    fileSuffix : '-info.md',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-00-Info.template",
    packageName : '', //calc by sub
    subPackageName : "info",
    subPath: '00-info',
]
INPUT.ITEMS.DTO = [
    filePrefix : '',
    fileSuffix : 'DTO.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-10-dto.template",
    packageName : '', //calc by sub
    subPackageName : "dto",
    subPath: 'dto',
]
INPUT.ITEMS.SEARCH_CRITERIA = [
    filePrefix : '',
    fileSuffix : 'SearchCriteria.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-12-SearchCriteria.template",
    packageName : '', //calc by sub
    subPackageName : "dto",
    subPath: 'dto',
]
INPUT.ITEMS.MYBATIS = [
    filePrefix : 'sql-',
    fileSuffix : 'Mapper.xml',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-20-mybatis.template",
    packageName : '', //calc by sub
    subPackageName : "",
    subPath: 'mybatis',
]

INPUT.ITEMS.MAPPER = [
    filePrefix : '',
    fileSuffix : 'Mapper.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-30-dao-mapper.template",
    packageName : '', //calc by sub
    subPackageName : "dao",
    subPath: 'dao',
]
INPUT.ITEMS.SERVICE = [
    filePrefix : '',
    fileSuffix : 'Service.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-40-service.template",
    packageName : '', //calc by sub
    subPackageName : "service",
    subPath: 'service',
]
INPUT.ITEMS.CONTROLLER = [
    filePrefix : '',
    fileSuffix : 'Controller.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-50-controller.template",
    packageName : '', //calc by sub
    subPackageName : "controller",
    subPath: 'controller',
]
INPUT.ITEMS.ServiceTest = [
    filePrefix : '',
    fileSuffix : 'ServiceTest.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-60-ServiceTest.template",
    packageName : '', //calc by sub
    subPackageName : "service",
    subPath: 'test_service',
]
INPUT.ITEMS.ControllerTest = [
    filePrefix : 'Test',
    fileSuffix : 'Controller.java',
    template : INPUT.TEMPLATE_BASE + "/wind-gen-62-ControllerTest.template",
    packageName : '', //calc by sub
    subPackageName : "mvc",
    subPath: 'test_controller',
]

//== angular: backoffice templates
INPUT.ITEMS.UiModel = [
    filePrefix : '',
    fileSuffix : '.model.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-70-UiModel.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceEnum = [
    filePrefix : '',
    fileSuffix : '.enum.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-71-UiServiceEnum.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceFile = [
    filePrefix : '',
    fileSuffix : '.service.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-72-UiServiceFile.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiServiceFile = [
    filePrefix : '',
    fileSuffix : '.service.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-72-UiServiceFile.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageTs = [
    filePrefix : '',
    fileSuffix : '.page.ts',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-73-UiPageTs.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageHtml = [
    filePrefix : '',
    fileSuffix : '.page.html',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-74-UiPageHtml.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

INPUT.ITEMS.UiPageScss = [
    filePrefix : '',
    fileSuffix : '.page.scss',
    fileUseClassNameLower : true, // className 소문자 사용
    template : INPUT.TEMPLATE_BASE + "/wind-gen-75-UiPageScss.template",
    packageName : '', //calc by sub
    subPackageName : "model",
    subPath: 'ui',
]

//=============== [e] inputs ===============


//=============== [s] main ===============
allTables = new HashMap<>()
allFields = new HashMap<>()

typeMapping = [
        (~/(?i)int/)                      : "Integer",
        (~/(?i)long/)                     : "Long",
        (~/(?i)number/)                   : "Integer",  //""String",
        (~/(?i)float|double|decimal|real/): "Double",
        (~/(?i)datetime|timestamp/)       : "java.util.Date", //"java.sql.Timestamp",
        (~/(?i)date/)                     : "java.util.Date", //"java.sql.Date",
        (~/(?i)time/)                     : "java.util.Date", //"java.sql.Time",
        (~/(?i)/)                         : "String"
]

// ui project type: angular
uiTypeMapping = [
        (~/(?i)int/)                      : "number",
        (~/(?i)long/)                     : "number",
        (~/(?i)number/)                   : "number",
        (~/(?i)float|double|decimal|real/): "number",
        (~/(?i)datetime|timestamp/)       : "string", //"java.sql.Timestamp",
        (~/(?i)date/)                     : "string", //"java.sql.Date",
        (~/(?i)time/)                     : "string", //"java.sql.Time",
        (~/(?i)/)                         : "string"
]

INSERT_FIELD_LIST = ["IPT_ID", "IPT_DTM", "IPT_IP"]
UPDATE_FIELD_LIST = ["UPD_ID", "UPD_DTM", "UPD_IP"]


//== template binding map
def getDefaultBinding(className, fields, table) {
    calcPackageName()

    //== template binding:
    def binding = getSimpleValueForBinding(className)

    // sub table object list : 자식 table 정보
    def subTableObjectList = getSubTableObjectList(className, table)
    binding.subTableObjectList = subTableObjectList
    binding.subTableObjectListExist = subTableObjectList != null && subTableObjectList.size() > 0

    return binding
}

// className에 대한 binding 정보
def getSimpleValueForBinding(className) {

    def binding = INPUT.clone()

    def table = allTables[className]
    def fields = allFields[className]

    // table 선택되지 않아서 값이 없으면 처리 안함
    if(table == null || fields == null) {
        return null
    }


    def nonPkFields = fields.findAll { !it.isPk }
    def pkFields = fields.findAll { it.isPk }
    def nonPkNoInsertFields = nonPkFields.findAll { !it.isInsertField }

    binding.className = className
    binding.classNameLower = javaName(table.getName(), false)
    //
    binding.fields = fields
    binding.pkFields = pkFields
    binding.nonPkFields = nonPkFields
    binding.nonPkNoInsertFields = nonPkNoInsertFields
    binding.pkFieldsFirst = pkFields.size() > 0 ? pkFields[0] : [:]
    //
    binding.table = table
    binding.tableComment = table.getComment()
    binding.sqlNs = "sql-${className}"
    binding.fullDTO = "${INPUT.ITEMS.DTO.packageName}.${className}DTO"
    binding.fullSearchCriteria = "${INPUT.ITEMS.DTO.packageName}.${className}SearchCriteria"

    return binding
}

// 자식 table 정보
def getSubTableObjectList(className, table) {
    def resultList = []
    def tableName = table.getName()

    if( INPUT != null
            && INPUT.relationship != null
            && INPUT.relationship.oneToMany != null
    ) {
        INPUT.relationship.oneToMany.eachWithIndex { it, index ->
            if( tableName.equals(it.tableOne) ) {
                def subTableName = it.tableMany
                assert subTableName != null
                def subClassName = javaName(subTableName, true)

                def subBinding = getSimpleValueForBinding(subClassName)
                if(INPUT.relationship.showErrorWhenNotSelectSubTable) {
                    assert subBinding != null
                }
                if(subBinding != null) {
                    resultList.add(subBinding)
                }
            }
        }
    }

    return resultList
}



//== bizName calculate: second word
def calcBizName(table) {
    def bizName = "biz"
    def str = table.getName()

    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }

    bizName = (s.size() == 1)? Case.LOWER.apply(s[0]) : Case.LOWER.apply(s[1])

    INPUT.bizName = bizName;
}

//== packageName calculate
def calcPackageName() {
    INPUT.ITEMS.each { key, val ->
        val.packageName = INPUT.packageNameBase + "." + INPUT.bizName + "." + val.subPackageName
    }
}

////== properties
//def loadProp(filePath) {
//    Properties prop = new Properties()
//    File propertiesFile = new File(filePath)
//    propertiesFile.withInputStream {
//        prop.load(it)
//    }
//
//    // test
//    assert prop.a == '1'
//
//    return prop;
//}


FILES.chooseDirectoryAndSave("Choose entity directory", "Choose where to store generated files") { dir ->
        //== fetch all tables
        SELECTION.filter { it instanceof DasTable && it.getKind() == ObjectKind.TABLE }.each {
            fetchAllDbInfo(it, dir)
        }

        //== generate for each
        INPUT.ITEMS.each {  entry ->
            def item = entry.value
            allTables.each { className, table ->

                //== 업무이름 계산: table명 두번째 필드로
                if(INPUT.bizNameDynamicYn) {
                    calcBizName(table)
                }

                //== package별로
                def baseDir = new File(dir, "${INPUT.bizName}")
                if(!baseDir.exists()) {
                    baseDir.mkdirs();
                }
                def newDir = new File(baseDir, "${item.subPath}")
                if(!newDir.exists()) {
                    newDir.mkdirs();
                }

                //== file name 처리
                def classNameLower = javaName(table.getName(), false)
                def fileClassName = className
                if(item.fileUseClassNameLower) {
                    fileClassName = classNameLower
                }
                def fileName = "${item.filePrefix}" + fileClassName + "${item.fileSuffix}"

                new File(newDir, "${fileName}").withPrintWriter { out ->
                    def fields = allFields[className];
                    _generateItem(out, className, fields, table, "${item.template}" )
                }
            }
        }
}

//=============== [e] main ===============

def fetchAllDbInfo(table, dir) {
    className = javaName(table.getName(), true)
    def fields = calcFields(table, className)
    allTables.put(className, table)
    allFields.put(className, fields)
}


def _generateItem(out, className, fields, table, templatePath) {

    def binding = getDefaultBinding(className, fields, table)

    //== template:
    def f = new File(templatePath)
    def engine = new groovy.text.GStringTemplateEngine()
    def template = engine.createTemplate(f).make(binding)
    //
    out.println template.toString()
}

// dtoTypeStr:  날짜는 String으로
def getDtoTypeStr(typeStr) {
    if(typeStr == "java.util.Date") {
        return "String"
    } else {
        return typeStr
    }
}


// fields
def calcFields(table, javaName) {
    DasUtil.getColumns(table).reduce([]) { fields, col ->
        def spec = Case.LOWER.apply(col.getDataType().getSpecification())
        def typeStr = typeMapping.find { p, t -> p.matcher(spec).find() }.value
        def uiTypeStr = uiTypeMapping.find { p, t -> p.matcher(spec).find() }.value  // ui type: angular
        def dtoTypeStr = getDtoTypeStr(typeStr)     //dto 에 사용할 타입
        def isDateType = (typeStr == "java.util.Date" ? true : false)   //날짜 타입 yn
        def isInsertField = INSERT_FIELD_LIST.contains(col.getName())   // insert column
        def isUpdateField = UPDATE_FIELD_LIST.contains(col.getName())   // update column

        def comm = [
                isPk : false,
                dbName : col.getName(),
                name : columnName(col.getName()),
                type : typeStr,
                uiTypeStr : uiTypeStr,
                dtoTypeStr : dtoTypeStr,
                isDateType : isDateType,
                isInsertField : isInsertField,
                isUpdateField : isUpdateField,
                annos: ["@Column(name = \"" + col.getName() + "\" )"],
                comment: col.getComment()
        ]
        if (table.getColumnAttrs(col).contains(DasColumn.Attribute.PRIMARY_KEY)) {
            comm.isPk = true
            comm.annos += ["@Id"]
            comm.annos += ["@GeneratedValue(strategy = GenerationType.IDENTITY)"]
        }
        fields += [comm]
    }
}


//== like camel case
def javaName(str, capitalize) {
    def s = ''

    def tmp = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
        .collect { Case.LOWER.apply(it).capitalize() }
    if(INPUT.removeTablePrefix) {
        //remove first prefix like t_
        tmp = tmp.subList(1,tmp.size())
    }
    s = tmp.join("")
        .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")

    capitalize || s.length() == 1 ? s : Case.LOWER.apply(s[0]) + s[1..-1]
}

//== like camel case
def columnName(str) {
    def s = com.intellij.psi.codeStyle.NameUtil.splitNameIntoWords(str)
            .collect { Case.LOWER.apply(it).capitalize() }
            .join("")
            .replaceAll(/[^\p{javaJavaIdentifierPart}[_]]/, "_")
    s.length() == 1? s : Case.LOWER.apply(s[0]) + s[1..-1]
}


//def generateComment(out, comment = null) {
//    out.println "/**"
//    out.println " * " + comment
//    out.println " *"
//    out.println " * <p>Date: " + new java.util.Date().toString() + "</p>"
//    out.println " */"
//}
//
//def toLowerCaseFirstOne(s){
//    if(Character.isLowerCase(s.charAt(0)))
//        return s;
//    else
//        return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
//}




