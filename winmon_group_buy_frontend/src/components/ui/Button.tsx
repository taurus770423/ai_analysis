import React from 'react';

// 簡易的 Tailwind 按鈕
type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement> & {
    variant?: 'primary' | 'outline';
    size?: 'sm' | 'md' | 'lg';
};

export const Button = ({
                           children,
                           className = '',
                           variant = 'primary',
                           size = 'md',
                           ...props
                       }: ButtonProps) => {
    const baseStyle = "font-bold rounded focus:outline-none focus:ring-2 focus:ring-opacity-50 transition-colors";

    const variantStyles = {
        primary: "bg-green-500 text-white hover:bg-green-600 focus:ring-green-400",
        outline: "bg-transparent text-white border border-white hover:bg-white hover:text-green-600",
    };

    const sizeStyles = {
        sm: "py-1 px-2 text-sm",
        md: "py-2 px-4",
        lg: "py-3 px-6 text-lg",
    };

    return (
        <button
            className={`${baseStyle} ${variantStyles[variant]} ${sizeStyles[size]} ${className}`}
            {...props}
        >
            {children}
        </button>
    );
};