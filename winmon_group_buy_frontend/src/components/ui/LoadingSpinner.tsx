// src/components/ui/LoadingSpinner.tsx
interface LoadingSpinnerProps {
    fullScreen?: boolean;
    text?: string;
}

const LoadingSpinner = ({ fullScreen = false, text = "Loading..." }: LoadingSpinnerProps) => {
    const spinner = (
        <div className="flex flex-col items-center justify-center space-y-2">
            <div className="w-12 h-12 border-4 border-t-green-500 border-gray-200 rounded-full animate-spin"></div>
            {text && <p className="text-gray-600">{text}</p>}
        </div>
    );

    if (fullScreen) {
        return (
            <div className="fixed inset-0 flex items-center justify-center bg-white bg-opacity-75 z-50">
                {spinner}
            </div>
        );
    }

    return spinner;
};

export default LoadingSpinner;