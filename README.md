## IDEA-Swagger-Knife4j-Plugin
Quickly generate [Swagger](https://io.swagger) 和 [Knife4j](https://doc.xiaominfo.com/) annotation IDEA plugins.

快速生成 [Swagger](https://io.swagger) 和 [Knife4j](https://doc.xiaominfo.com/) 注解的 IDEA 插件。

### Intro
- Generate annotations in batches
- Get JavaDoc and related annotation information automatically

- 批量生成注解
- 自动获取JavaDoc和相关注解的信息

### Guide
- Install Swagger-Knife4j-JavaDoc-Annotation-Toolkit plugin in IDEA.
- Select "Swagger Model Annotation" from the right-click menu "Generate ..." and select fields in the popup window to generate Swagger @ApiModelProperty annotations for them, and automatically fill in the JavaDoc comments, eliminating the annoying copying process. For String fields, also try to get the maximum field length from @Size or @Column and mark it with parentheses in the comment.

- 在 IDEA 中安装 Swagger-Knife4j-JavaDoc-Annotation-Toolkit 插件。
- 右键菜单"Generate..."中选择"Swagger Model Annotation", 在弹出窗口中选择字段, 即可为它们生成Swagger的 @ApiModelProperty 注解, 并自动填入JavaDoc的注释，省去烦人的拷贝过程。对于 String 字段， 还尝试从 @Size 或 @Column 中知道字段最大长度，并在注释中通过括号标注。

### TODO
- Generate Knife4j extension annotations for the Controller
- Generate Json by Bean with JavaDoc comments

- 为 Controller 生成 Knife4j 的扩展注解
- 按 Bean 生成 Json 并附上 JavaDoc 注释


### Contact
- HyphoonLee@tom.com

### License