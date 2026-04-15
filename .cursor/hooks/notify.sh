#!/bin/bash

# 读取 JSON 输入
input=$(cat)
status=$(echo "$input" | jq -r '.status')

# 发送桌面通知（需要 libnotify）
if [ "$status" = "completed" ]; then
  notify-send "✅ Cursor Agent" "主人，任务已完成喵" --urgency=normal --icon=dialog-information

elif [ "$status" = "aborted" ]; then
  notify-send "⚠️ Cursor Agent" "主人，任务已中止喵" --urgency=low --icon=dialog-warning

elif [ "$status" = "error" ]; then
  notify-send "❌ Cursor Agent" "主人，任务出现错误喵" --urgency=critical --icon=dialog-error
fi

exit 0