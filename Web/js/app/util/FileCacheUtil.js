/**
 * @author:wangkun
 * @date:2017-03-12
 * @description:文件缓存
 */
define(['Common'], function (com) {
    return {
        parentDic: null,
        isInit: false,
        tempDic: null,//临时目录
        datasetgrid: null,//临时文件
        Init: async function () {
            var me = this;
            window.requestFileSystem = window.requestFileSystem || window.webkitRequestFileSystem;
            var pro = new Promise(function (resolve, reject) {
                    window.requestFileSystem(window.TEMPORARY, 2 * 1024 * 1024 * 1024, function (fs) {
                    me.parentDic = fs.root;
                    resolve("suc");
                    me.isInit = true;
                }, function () {
                    resolve("err");
                });
            });
            return pro;
        },
        /**
		 * @author:wangkun
		 * @date:2017-08-02
		 * @param:
		 * @return:
		 * @description:创建目录
		 */
        CreateDir: async function (rootDir, folders) {
            var me = this;
            var folderSize = folders.length;
            var result = rootDir;
            for (var i = 0; i < folderSize; i++) {
                var folder = folders[i];
                result = await getDir(folder,result);
            }
            return result;
            async function getDir(folder,root) {
                return new Promise(function (resolve, reject) {
                    root.getDirectory(folder, { create: true }, function (dirEntry) {
                        resolve(dirEntry);
                    }, function () {
                        console.log("创建目录:" + folders + ",出错!");
                    });
                });
            }
        },
        /**
		 * @author:wangkun
		 * @date:2017-08-02
		 * @param:elementid-要素ID,hourspan-时效
		 * @return:
		 * @description:添加文件
		 */
        AddFileAsync: async function (pDir, strDateTime, elementid, hourspan, datasetgrid) {
            var me = this;
            if (!me.isInit) {
                console.log("缓存未初始化!");
                return;
            }
            var newDic = "";
            if(pDir != ""){
                newDic+=pDir+"/";
            }
            newDic += strDateTime + "/" + elementid + "/" + hourspan;
            var dirEntry = await me.CreateDir(me.parentDic, newDic.split('/'));
            var fileEntry = await getFile(dirEntry);
            var status = await createWriter(fileEntry,datasetgrid);
            if(status === "suc"){
                console.log(elementid+"保存成功!");
            }
            async function getFile(dirEntry){
                return new Promise(function (resolve, reject) {
                    dirEntry.getFile("data.txt", { create: true }, function (fileEntry) {
                        resolve(fileEntry);
                    });
                });
            }
            async function createWriter(fileEntry,datasetgrid){
                return new Promise(function (resolve, reject) {
                    fileEntry.createWriter(function (fileWriter) {
                        var str = JSON.stringify(datasetgrid);
                        var blob = new Blob([str], { type: "text/plain" });
                        fileWriter.write(blob);
                        resolve("suc");
                    });
                });
            }
        },
        /**
		 * @author:wangkun
		 * @date:2017-08-02
		 * @param:elementid-要素ID,hourspan-时效
		 * @return:
		 * @description:添加文件
		 */
        saveDatasetGridJson: function (parantDir,strDateTime, elementid, hourspan, strdatasetgrid, recall) {
            var me = this;
            if (!me.isInit) {
                console.log("缓存未初始化!");
                return;
            }
            var newDic = parantDir+"/"+strDateTime + "/" + elementid + "/" + hourspan;
            me.parentDic.getDirectory(newDic, {}, getDic);
            function getDic(dirEntry) {
                dirEntry.getFile("data.txt", {}, function (fileEntry) {
                    fileEntry.remove(function () {
                        dirEntry.getFile("data.txt", { create: true }, function (fileEntry) {
                            fileEntry.createWriter(function (fileWriter) {
                                var blob = new Blob([strdatasetgrid], { type: "text/plain" });
                                fileWriter.write(blob);
                                console.log(elementid + hourspan + "文件已保存");
                            });
                        });
                    });
                });
            }
        },
        /**
		 * @author:wangkun
		 * @date:2017-08-02
		 * @param:parentDic-父目录,strDateTime-制作时间,elementid-要素ID,hourspan-时效
		 * @return:
		 * @description:获取文件
		 */
        Get: async function (pDic, strDateTime, elementid, hourspan,level) {
            var me = this;
            var file = "";
            if(pDic!=""){
                file+=pDic+"/";
            }
            file += strDateTime + "/" + elementid + "/" + hourspan + "/" + level + "/data.txt";
            var fileEntry = await getFileEntry();
            if (fileEntry === "err") {
                return null;
            }
            var file = await getFile(fileEntry);
            var con = await getContent(file);
            return con;

            function getFileEntry(resolve, reject) {
                return new Promise(function (resolve, reject) {
                    me.parentDic.getFile(file, {}, function (fileEntry) {
                        resolve(fileEntry);
                    }, function (err) {
                        resolve("err");
                    });
                });
            }
            function getFile(fileEntry) {
                return new Promise(resolve => {
                    fileEntry.file(function (file) {
                        resolve(file);
                    });
                });
            }
            function getContent(file) {
                return new Promise(resolve => {
                    var reader = new FileReader();
                    reader.onloadend = function (data) {
                        resolve(this.result);
                    }
                    reader.readAsText(file);
                });
            }
        }
    }
});