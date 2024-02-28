#!/bin/bash

# 请将以下路径替换为实际路径
source_folder="/data/data/com.termux/files/home/weblog"
# 使用find递归查找所有ELF文件
find "$source_folder" -type f -exec bash -c '
  for file do
    # 检查文件是否是ELF文件
    if file "$file" | grep -q "ELF" ; then
      # 获取相对路径
      relative_path=$(basename "$file")
        
        if [[ "$relative_path" == *.so* ]]; then
 echo "文件 $file_name 具有 .so 后缀"
else
        mv "$file" "/data/data/com.termux/files/home/weblog/bin/$relative_path"
    
fi
      # 移动文件到目标文件夹


     # echo "移动文件: $relative_path"
    fi
  done
' bash {} +
