package com.alloy.cloud.plugin.gen.ui;

import com.alloy.cloud.plugin.gen.Cache;
import com.alloy.cloud.plugin.gen.GenerateEngine;
import com.alloy.cloud.plugin.gen.config.GlobalConfig;
import com.alloy.cloud.plugin.gen.entity.Selected;
import com.alloy.cloud.plugin.gen.util.StringUtil;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 代码生成参数选择器
 *
 * @author tn_kec
 * @since 2019-07-19
 */
public class TabSelectGenerateForm extends JDialog {
    /**
     * 主面板
     */
    private JPanel contentPane;
    /**
     * 确认按钮
     */
    private JButton buttonOK;
    /**
     * 取消按钮
     */
    private JButton buttonCancel;
    /**
     * 模块下拉框
     */
    private JComboBox<String> moduleComboBox;
    /**
     * 数据库选择
     */
    private JComboBox<String> databaseComboBox;
    /**
     * 作者字段
     */
    private JTextField authorField;
    /**
     * 包字段
     */
    private JTextField packageField;
    /**
     * 包选择按钮
     */
    private JButton packageChooseButton;
    /**
     * 模板全选框
     */
    private JCheckBox allCheckBox;
    /**
     * 模板面板
     */
    private JPanel templatePanel;
    /**
     * 所有模板复选框
     */
    private List<JCheckBox> checkBoxList = new ArrayList<>();
    /**
     * 当前项目中的module
     */
    private List<Module> moduleList;
    /**
     * 项目对象
     */
    private Project project;

    private GenerateEngine generateEngine;

    /**
     * 构造方法
     */
    public TabSelectGenerateForm(Project project, GenerateEngine generateEngine) {
        this.project = project;
        this.generateEngine = generateEngine;
        // 初始化module，存在资源路径的排前面
        this.moduleList = new LinkedList<>();
        for (Module module : ModuleManager.getInstance(project).getModules()) {
            // 存在源代码文件夹放前面，否则放后面
            if (existsSourcePath(module)) {
                this.moduleList.add(0, module);
            } else {
                this.moduleList.add(module);
            }
        }
        init();
        setTitle("Generate Code");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    /**
     * 获取已经选中的模板
     *
     * @return 模板对象集合
     */
    private List<String> getSelectedObjects() {
        // 获取到已选择的复选框
        List<String> selected = new ArrayList<>();
        checkBoxList.forEach(jCheckBox -> {
            if (jCheckBox.isSelected()) {
                selected.add(jCheckBox.getText());
            }
        });
        return selected;
    }

    /**
     * 初始化方法
     */
    private void init() {
        //添加模板组
        checkBoxList.clear();
        templatePanel.setLayout(new GridLayout(6, 2));

        for (String name : GlobalConfig.GENERATE_OBJECTS) {
            JCheckBox checkBox = new JCheckBox(name);
            checkBoxList.add(checkBox);
            templatePanel.add(checkBox);
        }
        //添加全选事件
        allCheckBox.addActionListener(e -> checkBoxList.forEach(jCheckBox -> jCheckBox.setSelected(allCheckBox.isSelected())));
        //初始化作者
        initAuthorComponent();
        //初始化module
        initModuleComponent();
        //初始化database
        initDatabaseComponent();
        //初始化路径
        initPackageComponent();
    }

    private void initAuthorComponent() {
        if (Cache.isNotEmpty("author")) {
            authorField.setText(Cache.getInstance().get("author"));
        } else {
            authorField.setText(System.getProperty("user.name"));
        }
    }

    private void initModuleComponent() {
        //初始化Module选择
        for (Module module : this.moduleList) {
            moduleComboBox.addItem(module.getName());
        }

        if (!Cache.isEmpty("module")) {
            //勾选默认选择
            moduleComboBox.setSelectedItem(Cache.getInstance().get("module"));
        }
    }

    private void initDatabaseComponent() {
        //初始化Database选择
        databaseComboBox.addItem("mysql");
//        databaseComboBox.addItem("oracle");

        if (!Cache.isEmpty("database")) {
            //勾选默认选择
            databaseComboBox.setSelectedItem(Cache.getInstance().get("database"));
        } else {
            databaseComboBox.setSelectedItem("mysql");
        }
    }

    private void initPackageComponent() {
        if (Cache.isNotEmpty("package")) {
            packageField.setText(Cache.getInstance().get("package"));
        } else {
            packageField.setText(findPackage());
        }

        //添加包选择事件
        packageChooseButton.addActionListener(e -> {
            Module module = getSelectModule();
            if (module == null) {
                Messages.showWarningDialog("Please select module!", "Generate Code");
                return;
            }
            PackageChooserDialog dialog = new PackageChooserDialog("Package Chooser", module);
            dialog.show();

            PsiPackage psiPackage = dialog.getSelectedPackage();
            if (psiPackage != null) {
                packageField.setText(psiPackage.getQualifiedName());
            }
        });
    }

    /**
     * 找打默认代码生成包路径
     *
     * @return
     */
    private String findPackage() {
        try {
            File f = findPackage(new File(getSelectModule().getModuleFilePath()));
            if (f != null) {
                int beginIndex = f.getPath().indexOf("\\src\\main\\java\\") + 15;
                return f.getPath().substring(beginIndex).replace("\\", ".");
            }
        } catch (Exception e) {
        }
        return "";
    }

    private File findPackage(File file) {
        File src = findSrcFile(file);
        if (src == null) {
            return null;
        }
        return findGeneratePackage(src);
    }

    private File findSrcFile(File file) {
        if (file == null) {
            return null;
        } else if ("src".equals(file.getName())) {
            return file;
        } else if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if ("src".equals(f.getName())) {
                    return f;
                }
            }
        }
        return findSrcFile(file.getParentFile());
    }

    private File findGeneratePackage(File file) {
        if (!file.isDirectory()) {
            return null;
        }
        for (File f : file.listFiles()) {
            if (f.getName().equals("dto")
                    || f.getName().equals("entity")
                    || f.getName().equals("mapper")
                    || f.getName().equals("service")) {
                return file;
            }
            File subFile = findGeneratePackage(f);
            if (subFile != null) {
                return subFile;
            }
        }
        return null;
    }

    /**
     * 选择的Module
     *
     * @return Module
     */
    private Module getSelectModule() {
        String name = (String) moduleComboBox.getSelectedItem();
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        return ModuleManager.getInstance(project).findModuleByName(name);
    }

    /**
     * 选择的数据库
     *
     * @return Database
     */
    private String getSelectDatabase() {
        return (String) databaseComboBox.getSelectedItem();
    }

    /**
     * 获取基本路径
     *
     * @return 基本路径
     */
    private String getBasePath() {
        Module module = getSelectModule();
        String baseDir = project.getBasePath();
        if (module != null) {
            baseDir = getSourcePath(module).getPath();
        }
        return baseDir;
    }

    /**
     * 获取模块的源代码文件夹，不存在
     *
     * @param module 模块对象
     * @return 文件夹路径
     */
    private VirtualFile getSourcePath(@NotNull Module module) {
        List<VirtualFile> virtualFileList = ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE);
        if (virtualFileList == null || virtualFileList.isEmpty()) {
            return VirtualFileManager.getInstance().findFileByUrl(String.format("file://%s", com.intellij.openapi.module.ModuleUtil.getModuleDirPath(module)));
        }
        return virtualFileList.get(0);
    }

    /**
     * 判断模块是否存在源代码文件夹
     *
     * @param module 模块对象
     * @return 是否存在
     */
    private boolean existsSourcePath(Module module) {
        List<VirtualFile> virtualFileList = ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE);
        return !(virtualFileList == null || virtualFileList.isEmpty());
    }

    /**
     * 获取目录
     */
    private String getPath() {
        String packageName = packageField.getText();
        // 获取基本路径
        String path = getBasePath();
        // 兼容Linux路径
        path = path.replace("\\", "/");
        // 如果存在包路径，添加包路径
        if (!StringUtil.isEmpty(packageName)) {
            path += "/" + packageName.replace(".", "/");
        }
        return path;
    }

    /**
     * 打开窗口
     */
    public void open() {
        this.pack();
        setLocationRelativeTo(null);
        this.setVisible(true);
    }

    /**
     * 确认按钮回调事件
     */
    private void onOK() {
        //缓存当前选择下次默认加载
        cacheSelected();

        Selected selected = getSelected();
        if (!checkSelected(selected)) {
            return;
        }

        //生成代码
        generateEngine.generate(selected);
        // 关闭窗口
        dispose();
    }

    private void cacheSelected() {
        Cache cache = Cache.getInstance();
        cache.set("author", authorField.getText());
        cache.set("module", moduleComboBox.getSelectedItem().toString());
        cache.set("database", databaseComboBox.getSelectedItem().toString());
        cache.set("package", packageField.getText());
    }

    private Selected getSelected() {
        Selected selected = new Selected();
        selected.setAuthor(authorField.getText());
        selected.setModule(getSelectModule().getName());
        selected.setDatabase(getSelectDatabase());
        selected.setPack(packageField.getText());
        selected.setPath(getPath().replace("\\", "/"));
        selected.setObjects(getSelectedObjects());

        return selected;
    }

    private boolean checkSelected(Selected selected) {
        //作者
        if (StringUtil.isEmpty(selected.getAuthor())) {
            Messages.showWarningDialog("Please select generate author!", "Generate Code");
            return false;
        }
        //选择包
        if (StringUtil.isEmpty(selected.getPack())) {
            Messages.showWarningDialog("Please select generate package!", "Generate Code");
            return false;
        }
        //选择路径
        if (StringUtil.isEmpty(selected.getPath())) {
            Messages.showWarningDialog("Please select generate path!", "Generate Code");
            return false;
        }
        //选中生成代码对象
        if (selected.getObjects().isEmpty()) {
            Messages.showWarningDialog("Please select generate object!", "Generate Code");
            return false;
        }

        return true;
    }

    /**
     * 取消按钮回调事件
     */
    private void onCancel() {
        dispose();
    }
}
