#!/usr/bin/env node

/**
 * ID类型转换脚本
 * 自动将TypeScript类型定义和代码中的ID字段从number转换为string
 * 使用方法: node scripts/convert-id-to-string.js [--dry-run]
 * 参考：https://github.com/chenshuai2144/openapi2typescript/issues/197
 */

const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

// 配置选项
const config = {
  // 是否为预览模式（不实际修改文件）
  dryRun: process.argv.includes('--dry-run') || process.argv.includes('-n'),

  // 需要处理的文件模式
  patterns: [
    'src/api/**/*.ts',
    'src/api/**/*.vue',
    'src/api/**/*.d.ts'
  ],

  // ID相关字段名
  idFields: [
    'id',
    'appId',
    'userId',
    'roleId',
    'parentId',
    'categoryId',
    'taskId',
    'orderId'
  ],

  // 需要转换的类型模式
  typeConversions: [
    // TypeScript类型定义中的ID字段
    {
      pattern: /(\w*[iI]d\??\s*:\s*)number/g,
      replacement: '$1string',
      description: 'ID字段类型定义'
    },

    // 函数参数中的ID类型
    {
      pattern: /(\(\s*\w*[iI]d\s*:\s*)number(\s*\))/g,
      replacement: '$1string$2',
      description: '函数参数ID类型'
    },

    // 变量声明中的ID类型
    {
      pattern: /(const\s+\w*[iI]d\s*:\s*)number/g,
      replacement: '$1string',
      description: '变量声明ID类型'
    }
  ],

  // 需要移除的类型转换
  codeConversions: [
    // parseInt(id) 转换
    {
      pattern: /parseInt\((\w*[iI]d(?:\.\w+)*)\)/g,
      replacement: '$1',
      description: '移除parseInt转换'
    },

    // Number(id) 转换
    {
      pattern: /Number\((\w*[iI]d(?:\.\w+)*)\)/g,
      replacement: '$1',
      description: '移除Number转换'
    },

    // +id 转换
    {
      pattern: /\+(\w*[iI]d(?:\.\w+)*)/g,
      replacement: '$1',
      description: '移除一元加号转换'
    },

    // .toString() 调用（当ID已经是string时不需要）
    {
      pattern: /(\w*[iI]d(?:\.\w+)*)\.toString\(\)/g,
      replacement: '$1',
      description: '移除不必要的toString调用'
    }
  ]
};

// 获取所有匹配的文件
function getMatchingFiles(patterns) {
  const files = new Set();

  // 递归搜索文件的函数
  function searchFiles(dir, extensions) {
    if (!fs.existsSync(dir)) return;

    const items = fs.readdirSync(dir, { withFileTypes: true });

    items.forEach(item => {
      const fullPath = path.join(dir, item.name);

      if (item.isDirectory()) {
        // 递归搜索子目录
        searchFiles(fullPath, extensions);
      } else if (item.isFile()) {
        // 检查文件扩展名
        const ext = path.extname(item.name);
        if (extensions.includes(ext)) {
          files.add(fullPath);
        }
      }
    });
  }

  // 搜索src目录下的所有TypeScript和Vue文件
  const srcDir = path.join(process.cwd(), 'src');
  const extensions = ['.ts', '.vue', '.d.ts'];

  try {
    searchFiles(srcDir, extensions);
  } catch (error) {
    console.warn(`Warning: Could not search files:`, error.message);
  }

  return Array.from(files).sort();
}

// 处理单个文件
function processFile(filePath) {
  const content = fs.readFileSync(filePath, 'utf8');
  let newContent = content;
  const changes = [];

  // 应用类型转换
  config.typeConversions.forEach(conversion => {
    const matches = newContent.match(conversion.pattern);
    if (matches) {
      newContent = newContent.replace(conversion.pattern, conversion.replacement);
      changes.push({
        type: 'type',
        description: conversion.description,
        count: matches.length
      });
    }
  });

  // 应用代码转换
  config.codeConversions.forEach(conversion => {
    const matches = newContent.match(conversion.pattern);
    if (matches) {
      newContent = newContent.replace(conversion.pattern, conversion.replacement);
      changes.push({
        type: 'code',
        description: conversion.description,
        count: matches.length
      });
    }
  });

  return {
    changed: content !== newContent,
    content: newContent,
    changes
  };
}

// 主函数
function main() {
  console.log('🔄 ID类型转换脚本开始运行...\n');

  if (config.dryRun) {
    console.log('📋 预览模式：只显示将要进行的更改，不会实际修改文件\n');
  }

  console.log('🔍 正在搜索文件...');
  const files = getMatchingFiles(config.patterns);
  console.log(`✅ 找到 ${files.length} 个文件\n`);

  let totalFiles = 0;
  let totalChanges = 0;
  const changesByType = {};

  // 处理每个文件
  files.forEach(filePath => {
    const result = processFile(filePath);

    if (result.changed) {
      totalFiles++;
      const fileChanges = result.changes.reduce((sum, change) => sum + change.count, 0);
      totalChanges += fileChanges;

      console.log(`📝 ${filePath}:`);
      result.changes.forEach(change => {
        console.log(`  - ${change.description}: ${change.count} 处`);
        changesByType[change.description] = (changesByType[change.description] || 0) + change.count;
      });

      if (!config.dryRun) {
        fs.writeFileSync(filePath, result.content, 'utf8');
        console.log(`  ✅ 已更新`);
      }
      console.log();
    }
  });

  // 显示总结
  console.log('📊 转换总结:');
  console.log(`  - 处理文件: ${files.length}`);
  console.log(`  - 修改文件: ${totalFiles}`);
  console.log(`  - 总更改数: ${totalChanges}`);
  console.log();

  if (Object.keys(changesByType).length > 0) {
    console.log('📋 更改详情:');
    Object.entries(changesByType).forEach(([type, count]) => {
      console.log(`  - ${type}: ${count} 处`);
    });
    console.log();
  }

  if (config.dryRun) {
    console.log('💡 要实际执行更改，请运行: node scripts/convert-id-to-string.js');
  } else if (totalFiles > 0) {
    console.log('🎉 ID类型转换完成！');
    console.log('💡 建议运行以下命令验证更改:');
    console.log('  - npm run type-check  # 检查类型错误');
    console.log('  - npm run build       # 构建测试');
    console.log('  - git diff            # 查看更改详情');
  } else {
    console.log('✨ 没有需要转换的ID类型，所有文件都是最新的！');
  }
}

// 异常处理
process.on('uncaughtException', (error) => {
  console.error('❌ 脚本执行出错:', error.message);
  process.exit(1);
});

process.on('unhandledRejection', (error) => {
  console.error('❌ 未处理的Promise拒绝:', error.message);
  process.exit(1);
});

// 运行主函数
main();
